package com.example.vchatserver.message;

import com.example.vchatserver.channel.ChannelRepository;
import com.example.vchatserver.channel.ChannelService;
import com.example.vchatserver.exceptions.ChatNotFoundException;
import com.example.vchatserver.exceptions.MessageNotFoundException;
import com.example.vchatserver.exceptions.NoRightsException;
import com.example.vchatserver.gateway.MessageGateway;
import com.example.vchatserver.group.Group;
import com.example.vchatserver.group.GroupRepository;
import com.example.vchatserver.group.GroupService;
import com.example.vchatserver.user.User;
import com.example.vchatserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    @Lazy
    GroupService groupService;
    @Autowired
    ChannelService channelService;

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    MessageGateway messageGateway;

    public Message addReader(Long userId, Long messageId) {
        Message message = get(messageId);
        if (!message.getReadersIds().contains(userId)) {
            message.getReadersIds().add(userId);
            return messageRepository.saveAndFlush(message);
        }
        return message;
    }

    public Message create(Long userId, CreateMessageDto dto) {
        if (!groupService.existsChat(dto.getMessageChatId())) {
            throw new ChatNotFoundException();
        }
        if (!userService.isMember(userId, dto.getMessageChatId())) {
            throw new NoRightsException();
        }
        // создаем сообщение
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setMessageChatId(dto.getMessageChatId());
        message.setCreationDate(ZonedDateTime.now());
        message.setReadersIds(new ArrayList<>(Collections.singletonList(userId)));
        message.setOwnerId(userId);
        Message messageToReturn = messageRepository.saveAndFlush(message);
        Group chat = groupService.getChat(messageToReturn.getMessageChatId());
        if (chat.getType() == 1) {
            groupService.addMessage(userId, messageToReturn.getMessageChatId(), messageToReturn.getId());
        } else {
            channelService.addMessage(userId, messageToReturn.getMessageChatId(), messageToReturn.getId());
        }
        for (Long memberId: groupService.getChat(message.getMessageChatId()).getMembersIds()) {
            messageGateway.notifyUserAboutNewMessage(memberId, messageToReturn.getContent(), messageToReturn.getMessageChatId(), chat.getType(), chat.getName(), chat.getImageData(), userService.get(messageToReturn.getOwnerId()).getName());
        }
        return messageToReturn;
    }

    public Message get(Long messageId) {
        try {
            return messageRepository.findById(messageId).get();
        } catch(Exception e) {
            throw new MessageNotFoundException();
        }
    }

    public boolean exists(Long messageId) {
        return messageRepository.existsById(messageId);
    }

    public List<User> getReaders(Long userId, Long messageId) {
        Message message = get(messageId);
        List<User> users = new ArrayList<>();
        for (Long readerId: message.getReadersIds()) {
            users.add(userService.get(readerId));
        }
        return users;
    }

    public List<Long> findMessagesIds(User user, Long chatId, String content) {
        return messageRepository.findMessagesIds(chatId, content);
    }

    public List<Integer> getPositionsOfFoundMessages(User user, Long chatId, String content) {
        if (content.equals("")) {
            return new ArrayList<>();
        }
        List<Integer> positions = new ArrayList<>();
        List<Long> chatMessagesIds = groupService.getChat(chatId).getMessagesIds();
        Collections.reverse(chatMessagesIds);
        for (Long foundChatId: findMessagesIds(user, chatId, content)) {
            if (chatMessagesIds.contains(foundChatId)) {
                positions.add(chatMessagesIds.indexOf(foundChatId));
            }
        }
        return positions;
    }

    public Message getLastMessage(Long userId, Long chatId) {
        Group chat = groupService.getChat(chatId);
        try {
            return get(chat.getMessagesIds().get(chat.getMessagesIds().size() - 1));
        } catch (Exception e) {
            return null;
        }
    }

    public List<Message> getMessagesWithOffset(User user, Long chatId, int limit, int offset) {
        return messageRepository.getMessagesWithOffset(chatId, limit, offset);
    }

    public void delete(Long userId, Long messageId) {
        Message message = get(messageId);
        Group group = groupService.getChat(message.getMessageChatId());
        List<Long> messagesIds = group.getMessagesIds();
        if (userService.canDeleteMessage(userId, messageId)) {
            messagesIds.remove(messageId);
            messageRepository.deleteById(messageId);
            group.setMessagesIds(messagesIds);
            if (group.getType() == 1) {groupRepository.saveAndFlush(group);}
            else {channelRepository.saveAndFlush(channelService.getByParent(group));}
            for (Long memberId: groupService.getChat(message.getMessageChatId()).getMembersIds()) {
                messageGateway.notifyUserAboutMessageDeleting(memberId, message.getMessageChatId());
            }
        } else {
            throw new NoRightsException();
        }
    }
}
