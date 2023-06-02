package com.example.vchatserver.user;

import com.example.vchatserver.channel.ChannelService;
import com.example.vchatserver.exceptions.*;
import com.example.vchatserver.group.Group;
import com.example.vchatserver.group.GroupRepository;
import com.example.vchatserver.group.GroupService;
import com.example.vchatserver.message.Message;
import com.example.vchatserver.message.MessageService;
import com.example.vchatserver.nickname.NicknameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.vchatserver.name.NameService.checkName;
import static com.example.vchatserver.name.NameService.ok;
import static com.example.vchatserver.password.PasswordService.checkPasswordConditions;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    NicknameService nicknameService;
    @Autowired
    @Lazy
    GroupService groupService;
    @Autowired
    @Lazy
    MessageService messageService;
    @Autowired
    @Lazy
    ChannelService channelService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User create(CreateUserDto createUserDto) {
        if (checkName(createUserDto.getName()) != ok) {
            throw new WrongNameException();
        } else if (nicknameService.checkNicknameForUser(createUserDto.getNickname()) != ok) {
            throw new WrongNicknameException();
        } else if (checkPasswordConditions(createUserDto.getPassword()) != ok) {
            throw new WrongPasswordException();
        } else if (createUserDto.getSecretWords().size() != 5) {
            throw new WrongSecretKeysException();
        } else {
            User user = new User();
            user.setName(createUserDto.getName());
            user.setNickname(createUserDto.getNickname().toLowerCase().strip());
            user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
            user.setImageData(createUserDto.getImageData());
            user.setChatsIds(new ArrayList<>());
            user.setSecretKeys(createUserDto.getSecretWords());
            user.setTypeOfImage(createUserDto.getTypeOfImage());
            return userRepository.saveAndFlush(user);
        }
    }

    public User get(String userNickname) {
        userNickname = userNickname.toLowerCase().strip();
        try {
            return userRepository.findByNickname(userNickname).get();
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public User get(Long userId) {
        try {
            return userRepository.findById(userId).get();
        } catch (Exception e) {
            throw new UserNotFoundException();
        }
    }

    public User getBaseInfo(Long userId) {
        User safeUserObject = get(userId);
        safeUserObject.setPassword("");
        safeUserObject.setChatsIds(new ArrayList<>());
        safeUserObject.setSecretKeys(new ArrayList<>());
        return safeUserObject;
    }

    public boolean exists(String userNickname) {
        return userRepository.existsByNickname(userNickname.toLowerCase().strip());
    }

    public boolean exists(Long userId) {
        return userRepository.existsById(userId);
    }

    public boolean isMember(Long userId, Long groupId) {
        return get(userId).getChatsIds().contains(groupId);
    }

    public List<Long> getChatsIds(Long userId) {

        List<Long> chatsIds = new ArrayList<>();
        for (Long chatId: get(userId).getChatsIds()) {
            if (groupService.existsChat(chatId)) {
                chatsIds.add(chatId);
            }
        }
        if (!chatsIds.equals(get(userId).getChatsIds())) {
            User user = get(userId);
            user.setChatsIds(chatsIds);
            userRepository.saveAndFlush(user);
        }
        return chatsIds;
    }

    public int getAmountOfChats(Long userId) {
        return userRepository.getAmountOfChats(userId);
    }

    public List<Group> getChats(Long userId) {
        List<Group> chats = new ArrayList<>();
        for (Long id: getChatsIds(userId)) {
            chats.add(groupService.getChatForUser(userId, id));
        }
        return chats;
    }

    public List<Long> getChatsIdsWithOffset(Long userId, int limit, int offset) {
        return userRepository.getChatsIdsWithOffset(userId, limit, offset);
    }

    public List<Group> getChatsWithOffset(Long userId, int limit, int offset) {
        List<Group> chats = new ArrayList<>();
        for (Long id: getChatsIdsWithOffset(userId, limit, offset)) {
            chats.add(groupService.getChatForUser(userId, id));
        }
        return chats;
    }

    public void changeName(Long userId, String newName) {
        User user = get(userId);
        if (checkName(newName) == ok) {
            user.setName(newName);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongNameException();
        }
    }

    public void changeNickname(Long userId, String newNickname) {
        newNickname = newNickname.toLowerCase().strip();
        User user = get(userId);
        if (nicknameService.checkNicknameForUser(newNickname) == ok) {
            user.setNickname(newNickname);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongNicknameException();
        }
    }

    public void changePassword(Long userId, String newPassword) {
        User user = get(userId);
        if (checkPasswordConditions(newPassword) == ok) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public void changePassword(String userNickname,
                               int a, String a_value,
                               int b, String b_value,
                               int c, String c_value,
                               String newPassword) {
        if (checkSecretKeys(userNickname, a, a_value, b, b_value, c, c_value)) {
            if (checkPasswordConditions(newPassword) == ok) {
                System.out.println("ok");
                User user = get(userNickname);
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.saveAndFlush(user);
            } else {
                throw new WrongPasswordException();
            }
        } else {
            throw new NoRightsException();
        }
    }

    public void changeSecretKeys(Long userId, List<String> secretKeys) {
        User user = get(userId);
        if (secretKeys.size() == 5) {
            user.setSecretKeys(secretKeys);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongSecretKeysException();
        }
    }

    public void changeImage(Long userId, String newImageData) {
        User user = get(userId);
        user.setImageData(newImageData);
        userRepository.saveAndFlush(user);
    }

    public void changeTypeOfImage(Long userId, int newTypeOfImage) {
        User user = get(userId);
        if (newTypeOfImage == 1 || newTypeOfImage == 2) {
            user.setTypeOfImage(newTypeOfImage);
            userRepository.saveAndFlush(user);
        } else {
            throw new WrongDataException();
        }
    }

    public boolean canWrite(Long userId, Long chatId) {
        return get(userId).getChatsIds().contains(chatId);
    }

    public boolean canEditChat(Long userId, Long chatId) {
        return groupService.getChat(chatId).getOwnerId().equals(userId);
    }

    public boolean canDeleteMessage(Long userId, Long messageId) {
        Message message = messageService.get(messageId);
        Group chat = groupService.getChat(message.getMessageChatId());
        if (chat.getType() == 1) {
            return groupService.canDeleteMessage(userId, messageId);
        } else {
            return channelService.canDeleteMessage(userId, messageId);
        }
    }

    public boolean canDeleteChat(User user, Long chatId) {
        return user.getChatsIds().contains(chatId);
    }

    public User addChat(Long userId, Long newChatId) {
        if (!groupService.existsChat(newChatId)) {
            throw new ChatNotFoundException();
        }
        User user = get(userId);
        if (!user.getChatsIds().contains(newChatId)) {
            user.getChatsIds().add(newChatId);
            if (groupService.getChat(newChatId).getType() == 1) {groupService.addMember(userId, newChatId);}
            else {channelService.addMember(userId, newChatId);}
        }
        return userRepository.saveAndFlush(user);
    }

    public User removeChat(Long userId, Long chatId) {
        Group group = groupService.getChat(chatId);
        User user = get(userId);
        if (group.getOwnerId().equals(userId)) {
            if (group.getType() == 1) {groupService.delete(userId, chatId);}
            else {channelService.delete(userId, chatId);}
        } else {
            if (group.getType() == 1) {groupService.removeMember(userId, chatId);}
            else {channelService.removeMember(userId, chatId);}
            user.getChatsIds().remove(chatId);
            return userRepository.saveAndFlush(user);
        }
        user.getChatsIds().remove(chatId);
        return user;
    }

    public boolean checkPassword(String userNickname, String verifiablePassword) {
        return passwordEncoder.matches(verifiablePassword, get(userNickname).getPassword());
    }

    public boolean checkSecretKeys(String userNickname,
                               int a, String a_value,
                               int b, String b_value,
                               int c, String c_value
    ) {
        User user = get(userNickname);
        return user.getSecretKeys().get(a).equals(a_value.strip()) &&
                user.getSecretKeys().get(b).equals(b_value.strip()) &&
                user.getSecretKeys().get(c).equals(c_value.strip());
    }

    public void delete(Long userId) {
        User user = get(userId);
        List<Long> chatsIds = new ArrayList<>(user.getChatsIds());
        for (Long chatId: chatsIds) {
            try {
                Group group = groupService.getChat(chatId);
                if (group.getOwnerId().equals(userId)) {
                    groupService.delete(userId, group.getId());
                } else {
                    groupService.removeMember(userId, group.getId());
                }
            } catch (Exception ignored) {}
        }
        userRepository.deleteById(user.getId());
    }
}
