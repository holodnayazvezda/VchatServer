package com.example.vchatserver.group;

import com.example.vchatserver.channel.ChannelRepository;
import com.example.vchatserver.channel.ChannelService;
import com.example.vchatserver.exceptions.*;
import com.example.vchatserver.gateway.MessageGateway;
import com.example.vchatserver.message.Message;
import com.example.vchatserver.message.MessageRepository;
import com.example.vchatserver.message.MessageService;
import com.example.vchatserver.user.User;
import com.example.vchatserver.user.UserRepository;
import com.example.vchatserver.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.vchatserver.name.NameService.checkName;
import static com.example.vchatserver.name.NameService.ok;

@Service
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    @Lazy
    MessageService messageService;
    @Autowired
    ChannelService channelService;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    MessageGateway messageGateway;

    public Group createGroup(Long ownerId, CreateGroupDto createGroupDto) {
        if (checkName(createGroupDto.getName()) != ok) {
            throw new WrongNameException();
        }
        List<Long> messagesList = new ArrayList<>();
        for (Long messageId: createGroupDto.getMessagesIds()) {
            if (!messageService.exists(messageId)) {
                throw new MessageNotFoundException();
            }
            if (!messagesList.contains(messageId)) {
                messagesList.add(messageId);
            }
        }
        List<Long> membersIds = new ArrayList<>();
        for (Long memberId: createGroupDto.getMembersIds()) {
            if (!userService.exists(ownerId)) {
                throw new UserNotFoundException();
            }
            if (!membersIds.contains(memberId)) {
                membersIds.add(memberId);
            }
        }
        if (!membersIds.contains(ownerId)) {
            membersIds.add(ownerId);
        }
        Group group = new Group();
        group.setName(createGroupDto.getName());
        group.setType(1);
        group.setImageData(createGroupDto.getImageData());
        group.setOwnerId(ownerId);
        group.setCreationDate(ZonedDateTime.now());
        group.setTypeOfImage(createGroupDto.getTypeOfImage());
        group.setMessagesIds(messagesList);
        group.setMembersIds(membersIds);
        group.setUnreadMsgCount(createGroupDto.getUnreadMsgCount());
        Group groupToReturn = groupRepository.saveAndFlush(group);
        userService.addChat(groupToReturn.getOwnerId(), group.getId());
        return groupToReturn;
    }

    public Group getById(Long groupId) {
        if (existsGroup(groupId)) {
            return groupRepository.findById(groupId).get();
        } else {throw new ChatNotFoundException();}
    }

    public Group getForUser(Long userId, Long groupId) {
        Group group = getById(groupId);
        group.setUnreadMsgCount(getUnreadMessagesCountForUser(userId, groupId));
        return group;
    }

    public Group getChat(Long chatId) {
        try {
            return getById(chatId);
        } catch (Exception e) {
            try {
                return channelService.getById(chatId);
            } catch (Exception err) {
                throw new ChatNotFoundException();
            }
        }
    }

    public List<Group> searchChatsWithOffset(String chatName, int limit, int offset) {
        return groupRepository.searchChatsWithOffset(chatName, limit, offset);
    }

    public Group getChatForUser(Long userId, Long chatId) {
        if (getChat(chatId).getType() == 1) {
            return getForUser(userId, chatId);
        }
        return channelService.getForUser(userId, chatId);
    }

    public boolean existsGroup(Long groupId) {
        return groupRepository.existsById(groupId);
    }

    public boolean existsChat(Long chatId) {
        return (existsGroup(chatId) || channelService.exists(chatId));
    }

    public Long getUnreadMessagesCountForUser(Long userId, Long groupId) {
        Group group = getById(groupId);
        long unreadMessagesCount = 0;
        for (int i = group.getMessagesIds().size() - 1; i > 0; i--) {
            try {
                Message message = messageService.get(group.getMessagesIds().get(i));
                if (message.getReadersIds().contains(userId)) {
                    break;
                } else {
                    unreadMessagesCount ++;
                }
            } catch (Exception e) {
                break;
            }
        }
        return unreadMessagesCount;
    }

    public Group addMember(Long userId, Long groupId) {
        Group group = getChat(groupId);
        if (userService.exists(userId)) {
            List<Long> membersIds = group.getMembersIds();
            if (!membersIds.contains(userId)) {
                membersIds.add(userId);
                group.setMembersIds(membersIds);
                for (Long messageId: group.getMessagesIds()) {
                    messageService.addReader(userId, messageId);
                }
                return groupRepository.saveAndFlush(group);
            } else {
                return group;
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public Group removeMember(Long userId, Long groupId) {
        Group group = getChat(groupId);
        if (userService.exists(userId)) {
            List<Long> messagesIds = new ArrayList<>();
            for (Long messageId: new ArrayList<>(group.getMessagesIds())) {
                Message message = messageService.get(messageId);
                if (message.getOwnerId().equals(userId)) {
                    removeMessage(userId, groupId, messageId);
                } else {
                    messagesIds.add(messageId);
                }
            }
            group.setMessagesIds(messagesIds);
            group.getMembersIds().remove(userId);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Group editName(Long userId, Long groupId, String newName) {
        Group group = getChat(groupId);
        if (group.getOwnerId().equals(userId)) {
            if (checkName(newName) == ok) {
                group.setName(newName);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new WrongNameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group editTypeOfImage(Long userId, Long groupId, Integer newTypeOfImage) {
        Group group = getChat(groupId);
        if (group.getOwnerId().equals(userId)) {
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                group.setTypeOfImage(newTypeOfImage);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new WrongDataException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Group editImage(Long userId, Long groupId, String imageData) {
        Group group = getChat(groupId);
        if (group.getOwnerId().equals(userId)) {
            group.setImageData(imageData);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public Group editAll(Long userId, Long groupId, String newName, Integer newTypeOfImage, String newImageData) {
        Group group = getById(groupId);
        if (group.getOwnerId().equals(userId)) {
            if (checkName(newName) == ok) {
                group.setName(newName);
            } else {throw new WrongNameException();}
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                group.setTypeOfImage(newTypeOfImage);
            } else {throw new WrongDataException();}
            group.setImageData(newImageData);
            return groupRepository.saveAndFlush(group);
        } else {
            throw new NoRightsException();
        }
    }

    public boolean canDeleteMessage(Long userId, Long messageId) {
        Message message = messageService.get(messageId);
        Group group = getById(message.getMessageChatId());
        return group.getMembersIds().contains(userId) &&
                group.getMessagesIds().contains(messageId) &&
                (group.getOwnerId().equals(userId) || message.getOwnerId().equals(userId));
    }

    public Group addMessage(Long userId, Long groupId, Long messageId) {
        if (messageService.exists(messageId)) {
            Group group = getChat(groupId);
            if (group.getMembersIds().contains(userId)) {
                List<Long> messageIds = group.getMessagesIds();
                if (!messageIds.contains(messageId)) {
                    messageIds.add(messageId);
                    group.setMessagesIds(messageIds);
                    return groupRepository.saveAndFlush(group);
                } else {
                    return group;
                }
            } else {
                throw new NoRightsException();
            }
        } else {
            throw new MessageNotFoundException();
        }
    }

    public Group removeMessage(Long userId, Long groupId, Long messageId) {
        if (messageService.exists(messageId)) {
            Group group = getChat(groupId);
            List<Long> messageIds = group.getMessagesIds();
            if (userService.canDeleteMessage(userId, messageId)) {
                messageIds.remove(messageId);
                messageRepository.deleteById(messageId);
                group.setMessagesIds(messageIds);
                return groupRepository.saveAndFlush(group);
            } else {
                throw new NoRightsException();
            }
        } else {
            throw new MessageNotFoundException();
        }
    }

    public void delete(Long userId, Long groupId) {
        Group group = getChat(groupId);
        if (group.getOwnerId().equals(userId)) {
            for (Long id: group.getMessagesIds()) {
                try {messageRepository.deleteById(id);} catch (Exception ignored) {System.out.println("errr");}
            }
            List<Long> membersIds = group.getMembersIds();
            for (Long memberId: membersIds) {
                try {
                    User user = userRepository.findById(memberId).get();
                    user.getChatsIds().remove(groupId);
                    userRepository.saveAndFlush(user);
                } catch (Exception ignored) {}
            }
            groupRepository.deleteById(groupId);
            for (Long memberId: group.getMembersIds()) {
                messageGateway.notifyUserAboutChatDeleting(memberId, groupId);
            }
        } else {
            throw new NoRightsException();
        }
    }
}
