package com.example.vchatserver.channel;

import com.example.vchatserver.exceptions.*;
import com.example.vchatserver.gateway.MessageGateway;
import com.example.vchatserver.group.Group;
import com.example.vchatserver.message.Message;
import com.example.vchatserver.message.MessageRepository;
import com.example.vchatserver.message.MessageService;
import com.example.vchatserver.nickname.NicknameService;
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
public class ChannelService {
    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    NicknameService nicknameService;
    @Autowired
    @Lazy
    UserService userService;
    @Autowired
    @Lazy
    MessageService messageService;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageGateway messageGateway;

    public Channel create(Long ownerId, CreateChannelDto dto) {
        if (checkName(dto.getName()) != ok) {
            throw new WrongNameException();
        } else if (nicknameService.checkNicknameForChannel(dto.getNickname()) != ok) {
            throw new WrongNicknameException();
        }
        List<Long> messagesList = new ArrayList<>();
        for (Long messageId: dto.getMessagesIds()) {
            if (!messageService.exists(messageId)) {
                throw new MessageNotFoundException();
            }
            if (!messagesList.contains(messageId)) {
                messagesList.add(messageId);
            }
        }
        List<Long> membersIds = new ArrayList<>();
        for (Long memberId: dto.getMembersIds()) {
            if (!userService.exists(memberId)) {
                throw new UserNotFoundException();
            }
            if (!membersIds.contains(memberId)) {
                membersIds.add(memberId);
            }
        }
        if (!membersIds.contains(ownerId)) {
            membersIds.add(ownerId);
        }
        Channel channel = new Channel();
        channel.setName(dto.getName());
        channel.setNickname(dto.getNickname().toLowerCase().strip());
        channel.setType(2);
        channel.setImageData(dto.getImageData());
        channel.setOwnerId(ownerId);
        channel.setCreationDate(ZonedDateTime.now());
        channel.setTypeOfImage(dto.getTypeOfImage());
        channel.setMessagesIds(dto.getMessagesIds());
        channel.setMembersIds(dto.getMembersIds());
        channel.setUnreadMsgCount(dto.getUnreadMsgCount());
        Channel channelToReturn = channelRepository.saveAndFlush(channel);
        userService.addChat(channelToReturn.getOwnerId(), channelToReturn.getId());
        return channelToReturn;
    }

    public Channel getById(Long channelId) {
        if (exists(channelId)) {
            return channelRepository.findById(channelId).get();
        } else {throw new ChatNotFoundException();}
    }

    public Channel getByParent(Group group) {
        try {
            Channel channel = channelRepository.findById(group.getId()).get();
            channel.setName(group.getName());
            channel.setTypeOfImage(group.getTypeOfImage());
            channel.setImageData(group.getImageData());
            channel.setOwnerId(group.getOwnerId());
            channel.setMessagesIds(group.getMessagesIds());
            channel.setMembersIds(group.getMembersIds());
            channel.setUnreadMsgCount(group.getUnreadMsgCount());
            return channel;
        } catch (Exception e) {
            throw new ChatNotFoundException();
        }
    }

    public Channel getForUser(Long userId, Long channelId) {
        Channel channel = getById(channelId);
        channel.setUnreadMsgCount(getUnreadMessagesCountForUser(userId, channelId));
        return channel;
    }

    public boolean exists(Long channelId) {
        return channelRepository.existsById(channelId);
    }


    public Long getUnreadMessagesCountForUser(Long userId, Long channelId) {
        Channel channel = getById(channelId);
        long unreadMessagesCount = 0;
        for (int i = channel.getMessagesIds().size() - 1; i > 0; i--) {
            try {
                Message message = messageService.get(channel.getMessagesIds().get(i));
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

    public Channel addMember(Long userId, Long channelId) {
        Channel channel = getById(channelId);
        if (userService.exists(userId)) {
            List<Long> membersIds = channel.getMembersIds();
            if (!membersIds.contains(userId)) {
                membersIds.add(userId);
                channel.setMembersIds(membersIds);
                for (Long messageId: channel.getMessagesIds()) {
                    messageService.addReader(userId, messageId);
                }
                return channelRepository.saveAndFlush(channel);
            } else {
                return channel;
            }
        } else {
            throw new UserNotFoundException();
        }
    }

    public Channel removeMember(Long userId, Long channelId) {
        Channel channel = getById(channelId);
        if (userService.exists(userId)) {
            List<Long> messagesIds = new ArrayList<>();
            for (Long messageId: new ArrayList<>(channel.getMessagesIds())) {
                Message message = messageService.get(messageId);
                if (message.getOwnerId().equals(userId)) {
                    removeMessage(userId, channelId, messageId);
                } else {
                    messagesIds.add(messageId);
                }
            }
            channel.setMessagesIds(messagesIds);
            channel.getMembersIds().remove(userId);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Channel editName(Long userId, Long channelId, String newName) {
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            if (checkName(newName) == ok) {
                channel.setName(newName);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongNameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editNickname(Long userId, Long channelId, String newNickname) {
        newNickname = newNickname.toLowerCase().strip();
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            if (nicknameService.checkNicknameForChannel(newNickname) == ok || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongNicknameException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editTypeOfImage(Long userId, Long channelId, Integer newTypeOfImage) {
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new WrongDataException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editImage(Long userId, Long channelId, String imageData) {
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            channel.setImageData(imageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel editAll(Long userId, Long channelId, String newName, String newNickname, Integer newTypeOfImage, String newImageData) {
        newNickname = newNickname.toLowerCase().strip();
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            if (checkName(newName) == ok) {
                channel.setName(newName);
            } else {throw new WrongNameException();}
            if (nicknameService.checkNicknameForChannel(newNickname) == 200 || channel.getNickname().equals(newNickname)) {
                channel.setNickname(newNickname);
            } else {throw new WrongNicknameException();}
            if (newTypeOfImage == 1 || newTypeOfImage == 2) {
                channel.setTypeOfImage(newTypeOfImage);
            } else {throw new WrongDataException();}
            channel.setImageData(newImageData);
            return channelRepository.saveAndFlush(channel);
        } else {
            throw new NoRightsException();
        }
    }

    public Channel addMessage(Long userId, Long channelId, Long messageId) {
        if (messageService.exists(messageId)) {
            Channel channel = getById(channelId);
            if (channel.getOwnerId().equals(userId)) {
                List<Long> messageIds = channel.getMessagesIds();
                if (!messageIds.contains(messageId)) {
                    messageIds.add(messageId);
                    channel.setMessagesIds(messageIds);
                    return channelRepository.saveAndFlush(channel);
                } else {
                    return channel;
                }
            } else {
                throw new NoRightsException();
            }
        } else {
            throw new MessageNotFoundException();
        }
    }


    public Channel removeMessage(Long userId, Long channelId, Long messageId) {
        if (messageService.exists(messageId)) {
            Channel channel = getById(channelId);
            List<Long> messageIds = channel.getMessagesIds();
            if (canDeleteMessage(userId, messageId)) {
                messageIds.remove(messageId);
                messageRepository.deleteById(messageId);
                channel.setMessagesIds(messageIds);
                return channelRepository.saveAndFlush(channel);
            } else {
                throw new NoRightsException();
            }
        } else {
            throw new MessageNotFoundException();
        }
    }

    public boolean canDeleteMessage(Long userId, Long messageId) {
        Message message = messageService.get(messageId);
        Channel channel = getById(message.getMessageChatId());
        return channel.getOwnerId().equals(userId) &&
                channel.getMessagesIds().contains(messageId);
    }

    public void delete(Long userId, Long channelId) {
        Channel channel = getById(channelId);
        if (channel.getOwnerId().equals(userId)) {
            for (Long id: channel.getMessagesIds()) {
                try {messageRepository.deleteById(id);} catch (Exception ignored) {}
            }
            List<Long> membersIds = channel.getMembersIds();
            for (Long memberId: membersIds) {
                try {
                    User user = userService.get(memberId);
                    user.getChatsIds().remove(channelId);
                    userRepository.saveAndFlush(user);
                } catch (Exception ignored) {}
            }
            channelRepository.deleteById(channelId);
            for (Long memberId: channel.getMembersIds()) {
                messageGateway.notifyUserAboutChatDeleting(memberId, channelId);
            }
        } else {
            throw new NoRightsException();
        }
    }
}
