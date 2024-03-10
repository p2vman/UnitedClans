package unitedclans.langs;

import org.bukkit.configuration.file.YamlConfiguration;

public class Langs {
    public enum LangEN {
        INVALID_COMMAND("messages.invalid-command", "§cThe command was entered incorrectly "),
        WRONG_CLAN_NAME("messages.wrong-clan-name", "§cWrong clan name "),
        LENGTH_CLAN_NAME("messages.length-clan-name", "§cThe clan name can only be in the range from 3 to 12 characters "),
        WRONG_COLOR_NAME("messages.wrong-color-name", "§cWrong color name "),
        WRONG_PLAYER_NAME("messages.wrong-player-name", "§cWrong player name "),
        WRONG_ROLE_NAME("messages.wrong-role-name", "§cWrong role name "),
        CLAN_NAME_TAKEN("messages.clan_name_taken", "§eThis clan name is already taken "),
        YOU_MEMBER_CLAN("messages.you-member-clan", "§eYou are already a member of a clan "),
        YOU_NOT_MEMBER_CLAN("messages.you-not-member-clan", "§eYou are not a member of a clan "),
        YOU_NOT_MEMBER_THIS_CLAN("messages.you-not-member-this-clan", "§eYou are not a member of this clan "),
        SUCCESS_CREATE_CLAN("messages.success-create-clan", "§bYou have successfully created the§r §e§l%clan%§r §bclan "),
        NOT_LEADER("messages.not-leader", "§eYou are not the leader of this clan "),
        NO_RIGHTS_INVITE("messages.no-rights-invite", "§eYou do not have enough rights to invite a player "),
        NO_RIGHTS_SET_ROLE("messages.no_rights_set_role", "§eYou do not have enough rights to change the player role "),
        NO_RIGHTS_KICK("messages.no_rights_kick", "§eYou do not have enough rights to kick a player "),
        ALREADY_SENT_INVITATION("messages.already_sent_invitation", "§eAt the moment, someone has already sent an invitation to this player "),
        INVITATION_SENT("messages.invitation-sent", "§bThe invitation has been sent "),
        SUCCESS_DELETE_CLAN("messages.success_delete_clan", "§bThe§r §e§l%clan%§r §bclan was successfully deleted "),
        LEADER_DELETE_CLAN("messages.leader-delete-clan", "§bThe leader of your clan has decided to delete the clan "),
        PLAYER_MEMBER_CLAN("messages.player-member-clan", "§eThis player is already in the clan "),
        PLAYER_NOT_MEMBER_CLAN("messages.player-not-member-clan", "§eThe player is not a member of a clan "),
        INVITATION("messages.invitation", "§bYou have received an invitation to the§r §e§l%clan%§r §bclan from the player§r §e§l%player% "),
        CLICK_INVITE("messages.click-invite", "Click to accept the invitation "),
        ACCEPT("messages.accept", "§bAccept invitation§r §e§l[✔] "),
        YOU_NOT_INVITED("messages.you-not-invited", "§eYou have no active invitations "),
        THIS_CLAN_MAX("messages.this_clan_max", "§eThis clan already has the maximum number of players "),
        YOUR_CLAN_MAX("messages.your-clan-max", "§eYour clan already has the maximum number of players "),
        SEND_INVITATION_YOURSELF("messages.send-invitation-yourself", "§eYou cannot send an invitation to yourself "),
        SUCCESSFULLY_JOINED_CLAN("messages.successfully-joined-clan", "§bYou have successfully joined the clan§r §e§l%clan% "),
        PLAYER_JOINED("messages.player-joined", "§bPlayer§r §e§l%player%§r §bjoined the clan "),
        PLAYER_NOT_YOUR_CLAN("messages.player-not-your-clan", "§eThis player is not a member of your clan "),
        ROLE_IS_HIGHER("messages.role_is_higher", "§eYou cannot kick a player because his role is higher or the same as yours "),
        PLAYER_WAS_KICKED("messages.player-was-kicked", "§bPlayer§r §e§l%player%§r §bwas kicked out of the clan "),
        KICK_YOURSELF("messages.kick-yourself", "§eYou cannot kick yourself out "),
        YOU_WAS_KICKED("messages.you-was-kicked", "§bYou were kicked out of the§r §e§l%clan%§r §bclan "),
        YOU_LEADER("messages.you-leader", "§eYou cannot leave a clan because you are the leader of that clan "),
        PLAYER_LEAVE("messages.player-leave", "§bPlayer§r §e§l%player%§r §bleft the clan "),
        SUCCESSFULLY_LEFT("messages.successfully-left", "§bYou have successfully left the§r §e§l%clan%§r §bclan "),
        SET_ROLE_YOURSELF("messages.set-role-yourself", "§eYou cannot change your role "),
        YOU_BEEN_ASSIGNED("messages.you-been-assigned", "§bYou have been assigned the role of§r §e§l%role% "),
        SUCCESSFULLY_CHANGED_ROLE("messages.successfully-changed-role", "§bYou have successfully changed the player role to§r §e§l%role% "),

        CLAN_MENU("menu-gui.clan-menu.inventory-name", "Clan menu "),
        CLAN_BANK("menu-gui.clan-menu.clan-bank", "Clan bank "),
        CLAN_BANK_DESCRIPTION("menu-gui.clan-menu.clan-bank-description", "Opens the clan bank menu "),
        CLAN_MEMBERS("menu-gui.clan-menu.clan-members", "Clan members "),
        CLAN_MEMBERS_DESCRIPTION("menu-gui.clan-menu.clan-members-description", "Opens list of clan members "),
        TOP_CLANS("menu-gui.clan-menu.top-clans", "Top clans "),
        TOP_CLANS_DESCRIPTION("menu-gui.clan-menu.top-clans-description", "Opens list of the best clans "),

        MEMBERS_MENU("menu-gui.members-menu.inventory-name", "Members menu "),
        MEMBERS_BACK_TO_MENU("menu-gui.members-menu.members-back-to-menu", "Go back "),
        MEMBERS_BACK_TO_MENU_DESCRIPTION("menu-gui.members-menu.members-back-to-menu-description", "Opens main clan menu "),

        MEMBER_MENU("menu-gui.member-menu.inventory-name", "Member menu "),
        CHANGE_ROLE("menu-gui.member-menu.change-role", "Change role "),
        CHANGE_ROLE_DESCRIPTION("menu-gui.member-menu.change-role-description", "Changes role "),
        KICK_MEMBER("menu-gui.member-menu.kick-member", "Kick member "),
        KICK_MEMBER_DESCRIPTION("menu-gui.member-menu.kick-member-description", "Kicks member out of the clan "),
        MEMBER_BACK_TO_MENU("menu-gui.member-menu.member-back-to-menu", "Go back "),
        MEMBER_BACK_TO_MENU_DESCRIPTION("menu-gui.member-menu.member-back-to-menu-description", "Opens members menu "),

        LEADER("roles.leader", "Leader "),
        ELDER("roles.elder", "Elder "),
        MEMBER("roles.member", "Member "),
        NO_CLAN("roles.no-clan", "null ");
        private String path;
        private String msg;
        private static YamlConfiguration LANG;

        LangEN(String path, String msg) {
            this.path = path;
            this.msg = msg;
        }

        public static void setFile(YamlConfiguration config) {
            LANG = config;
        }

        @Override
        public String toString() {
            return LANG.getString(path, msg);
        }

        public String getMsg() {
            return this.msg;
        }
        public String getPath() {
            return this.path;
        }
    }

    public enum LangRU {
        INVALID_COMMAND("messages.invalid-command", "§cНекорректная команда "),
        WRONG_CLAN_NAME("messages.wrong-clan-name", "§cНеверное название клана "),
        LENGTH_CLAN_NAME("messages.length-clan-name", "§cНазвание клана может содержать от 3 до 12 символов "),
        WRONG_COLOR_NAME("messages.wrong-color-name", "§cНеверное название цвета "),
        WRONG_PLAYER_NAME("messages.wrong-player-name", "§cНеверное имя игрока "),
        WRONG_ROLE_NAME("messages.wrong-role-name", "§cНеверное название роли "),
        CLAN_NAME_TAKEN("messages.clan_name_taken", "§eЭто название клана уже занято "),
        YOU_MEMBER_CLAN("messages.you-member-clan", "§eВы уже состоите в клане "),
        YOU_NOT_MEMBER_CLAN("messages.you-not-member-clan", "§eВы не состоите в клане "),
        YOU_NOT_MEMBER_THIS_CLAN("messages.you-not-member-this-clan", "§eВы не состоите в этом клане "),
        SUCCESS_CREATE_CLAN("messages.success-create-clan", "§bВы успешно создали клан§r §e§l%clan% "),
        NOT_LEADER("messages.not-leader", "§eВы не являетесь лидером этого клана "),
        NO_RIGHTS_INVITE("messages.no-rights-invite", "§eУ вас недостаточно прав, чтобы пригласить игрока "),
        NO_RIGHTS_SET_ROLE("messages.no_rights_set_role", "§eУ вас недостаточно прав для изменения роли игрока "),
        NO_RIGHTS_KICK("messages.no_rights_kick", "§eУ вас недостаточно прав, чтобы выгнать игрока "),
        ALREADY_SENT_INVITATION("messages.already_sent_invitation", "§eНа данный момент этому игроку кто-то уже отправил приглашение "),
        INVITATION_SENT("messages.invitation-sent", "§bПриглашение отправлено "),
        SUCCESS_DELETE_CLAN("messages.success_delete_clan", "§bКлан§r §e§l%clan%§r §bуспешно удален "),
        LEADER_DELETE_CLAN("messages.leader-delete-clan", "§bЛидер вашего клана решил удалить клан "),
        PLAYER_MEMBER_CLAN("messages.player-member-clan", "§eЭтот игрок уже состоит в клане "),
        PLAYER_NOT_MEMBER_CLAN("messages.player-not-member-clan", "§eИгрок не состоит в клане "),
        INVITATION("messages.invitation", "§bВы получили приглашение в клан§r §e§l%clan%§r §bот игрока§r §e§l%player% "),
        CLICK_INVITE("messages.click-invite", "Нажмите, чтобы принять приглашение "),
        ACCEPT("messages.accept", "§bПринять приглашение§r §e§l[✔] "),
        YOU_NOT_INVITED("messages.you-not-invited", "§eУ вас нет активных приглашений "),
        THIS_CLAN_MAX("messages.this_clan_max", "§eВ этом клане уже максимальное количество игроков "),
        YOUR_CLAN_MAX("messages.your-clan-max", "§eВ вашем клане уже максимальное количество игроков "),
        SEND_INVITATION_YOURSELF("messages.send-invitation-yourself", "§eВы не можете отправить приглашение самому себе "),
        SUCCESSFULLY_JOINED_CLAN("messages.successfully-joined-clan", "§bВы успешно вступили в клан§r §e§l%clan% "),
        PLAYER_JOINED("messages.player-joined", "§bИгрок§r §e§l%player%§r §bвступил в клан "),
        PLAYER_NOT_YOUR_CLAN("messages.player-not-your-clan", "§eЭтот игрок не состоит в вашем клане "),
        ROLE_IS_HIGHER("messages.role_is_higher", "§eВы не можете выгнать игрока, потому что его роль выше или такая же, как ваша "),
        PLAYER_WAS_KICKED("messages.player-was-kicked", "§bИгрок§r §e§l%player%§r §bбыл выгнан из клана "),
        KICK_YOURSELF("messages.kick-yourself", "§eВы не можете выгнать себя "),
        YOU_WAS_KICKED("messages.you-was-kicked", "§bВас выгнали из клана§r §e§l%clan% "),
        YOU_LEADER("messages.you-leader", "§eВы не можете покинуть клан, потому что вы являетесь лидером этого клана "),
        PLAYER_LEAVE("messages.player-leave", "§bИгрок§r §e§l%player%§r §bпокинул клан "),
        SUCCESSFULLY_LEFT("messages.successfully-left", "§bВы успешно покинули клан§r §e§l%clan% "),
        SET_ROLE_YOURSELF("messages.set-role-yourself", "§eВы не можете изменить свою роль "),
        YOU_BEEN_ASSIGNED("messages.you-been-assigned", "§bВам была присвоена роль§r §e§l%role% "),
        SUCCESSFULLY_CHANGED_ROLE("messages.successfully-changed-role", "§bВы успешно изменили роль игрока на§r §e§l%role% "),

        CLAN_MENU("menu-gui.clan-menu.inventory-name", "Меню клана "),
        CLAN_BANK("menu-gui.clan-menu.clan-bank", "Банк клана "),
        CLAN_BANK_DESCRIPTION("menu-gui.clan-menu.clan-bank-description", "Открывает меню банка клана "),
        CLAN_MEMBERS("menu-gui.clan-menu.clan-members", "Меню участников "),
        CLAN_MEMBERS_DESCRIPTION("menu-gui.clan-menu.clan-members-description", "Открывает список участников "),
        TOP_CLANS("menu-gui.clan-menu.top-clans", "Топ кланов "),
        TOP_CLANS_DESCRIPTION("menu-gui.clan-menu.top-clans-description", "Открывает список лучших кланов "),

        MEMBERS_MENU("menu-gui.members-menu.inventory-name", "Меню участников "),
        MEMBERS_BACK_TO_MENU("menu-gui.members-menu.members-back-to-menu", "Вернуться назад "),
        MEMBERS_BACK_TO_MENU_DESCRIPTION("menu-gui.members-menu.members-back-to-menu-description", "Открывает основное меню клана "),

        MEMBER_MENU("menu-gui.member-menu.inventory-name", "Меню участника "),
        CHANGE_ROLE("menu-gui.member-menu.change-role", "Сменить роль "),
        CHANGE_ROLE_DESCRIPTION("menu-gui.member-menu.change-role-description", "Меняет роль "),
        KICK_MEMBER("menu-gui.member-menu.kick-member", "Выгнать участника "),
        KICK_MEMBER_DESCRIPTION("menu-gui.member-menu.kick-member-description", "Выгоняет участника из клана "),
        MEMBER_BACK_TO_MENU("menu-gui.member-menu.member-back-to-menu", "Вернуться назад "),
        MEMBER_BACK_TO_MENU_DESCRIPTION("menu-gui.member-menu.member-back-to-menu-description", "Открывает меню участников "),

        LEADER("roles.leader", "Глава "),
        ELDER("roles.elder", "Старейшина "),
        MEMBER("roles.member", "Участник "),
        NO_CLAN("roles.no-clan", "null ");
        private String path;
        private String msg;
        private static YamlConfiguration LANG;

        LangRU(String path, String msg) {
            this.path = path;
            this.msg = msg;
        }

        public static void setFile(YamlConfiguration config) {
            LANG = config;
        }

        @Override
        public String toString() {
            return LANG.getString(path, msg);
        }

        public String getMsg() {
            return this.msg;
        }
        public String getPath() {
            return this.path;
        }
    }
}