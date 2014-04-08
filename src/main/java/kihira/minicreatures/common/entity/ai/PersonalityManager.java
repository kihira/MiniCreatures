package kihira.minicreatures.common.entity.ai;

import net.minecraft.entity.EntityLiving;

import java.util.ArrayList;
import java.util.List;

public class PersonalityManager {

    private final EntityLiving theEntity;
    private final int timeBetweenIdleMessages;

    private int ticksSinceLastIdleMessage = 0;
    private int ticksIdleMessageActive = 0;

    private int ticksSinceLastAttackMessage = 0;

    private final ArrayList<PersonalityAIIdleBase> idleAI = new ArrayList<PersonalityAIIdleBase>();

    public PersonalityManager(EntityLiving theEntity, int timeBetweenIdleMessages) {
        this.theEntity = theEntity;
        this.timeBetweenIdleMessages = timeBetweenIdleMessages;
    }

    public void addIdleAI(PersonalityAIIdleBase aiIdleBase) {
        this.idleAI.add(aiIdleBase);
    }

    public String getIdleMessage() {
        List<Message> messages = new ArrayList<Message>();
        for (PersonalityAIIdleBase idleAI : this.idleAI) {
            if (idleAI.shouldUpdate()) {
                idleAI.onUpdate();
                Message message = idleAI.getMessage();
                if (message != null) {
                    messages.add(message);
                }
            }
        }
        if (this.ticksSinceLastIdleMessage > this.timeBetweenIdleMessages && this.ticksIdleMessageActive == 0) {
        }
        return null;
    }

    public String getAttackMessage() {
        return null;
    }

    public class Message {
        private String message;
        private int messagePriority;
        private int messageDuration;

        public Message(int messagePriority, String message, int messageDuration) {
            this.messagePriority = messagePriority;
            this.message = message;
            this.messageDuration = messageDuration;
        }

        public String getMessage() {
            return this.message;
        }

        public int getMessagePriority() {
            return this.messagePriority;
        }

        public int getMessageDuration() {
            return this.messageDuration;
        }
    }
}
