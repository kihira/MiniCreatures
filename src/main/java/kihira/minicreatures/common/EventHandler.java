package kihira.minicreatures.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.network.MiniCreaturesMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class EventHandler {

    private long lastTrigger = 0;

    @SubscribeEvent
    public void leftClick(InputEvent.MouseInputEvent interactEvent) {
        //Only trigger if it has been more then 5 seconds
        if ((System.currentTimeMillis() - this.lastTrigger > 5000) && Minecraft.getMinecraft().gameSettings.keyBindAttack.getIsKeyPressed()) {
            //Only if it's a sword
            if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                MovingObjectPosition target = getMouseOver(20);
                if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    this.lastTrigger = System.currentTimeMillis();
                    MiniCreatures.packetHandler.sendToServer(new MiniCreaturesMessage.SetAttackTargetMessage(target.entityHit.getEntityId()));
                }
            }
        }
    }

    public MovingObjectPosition getMouseOver(double distance) {
        MovingObjectPosition objectMouseOver = null;
        if ((Minecraft.getMinecraft().renderViewEntity != null) && (Minecraft.getMinecraft().theWorld != null)) {
            Entity pointedEntity = null;
            objectMouseOver = Minecraft.getMinecraft().renderViewEntity.rayTrace(distance, 1);
            Vec3 vec3 = Minecraft.getMinecraft().renderViewEntity.getPosition(1);

            if (objectMouseOver != null) distance = objectMouseOver.hitVec.distanceTo(vec3);

            Vec3 vec31 = Minecraft.getMinecraft().renderViewEntity.getLook(1);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
            Vec3 vec33 = null;
            List list = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().renderViewEntity, Minecraft.getMinecraft().renderViewEntity.boundingBox.addCoord(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance).expand(1, 1, 1));
            double d2 = distance;

            for (Object aList : list) {
                Entity entity = (Entity) aList;

                if (entity.canBeCollidedWith()) {
                    float f2 = entity.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double) f2, (double) f2, (double) f2);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3)) {
                        if (0.0D < d2 || d2 == 0.0D) {
                            pointedEntity = entity;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity == Minecraft.getMinecraft().renderViewEntity.ridingEntity && !entity.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (d2 < distance || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
        }
        return objectMouseOver;
    }
}
