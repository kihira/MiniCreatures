/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.minicreatures.client;

// todo port to 1.9.4
public class TailsCompatHandler {

    public TailsCompatHandler() {
        //RenderPart.registerRenderHelper(EntityMiniPlayer.class, new MiniPlayerRenderHelper());
    }

/*    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderTick(RenderLivingEvent.Specials.Pre e) {
        if (e.entity instanceof EntityMiniPlayer && ((EntityMiniPlayer) e.entity).hasCustomNameTag()) {
            UUID uuid = e.entity.getPersistentID();
            if (!Tails.proxy.hasPartsData(uuid)) {
                EntityMiniPlayer entityMiniPlayer = (EntityMiniPlayer) e.entity;
                ResourceLocation resourcelocation = AbstractClientPlayer.getLocationSkin(entityMiniPlayer.getCustomNameTag());
                ThreadDownloadImageData imageData = AbstractClientPlayer.getDownloadImageSkin(resourcelocation, entityMiniPlayer.getCustomNameTag());
                if (imageData != null) {
                    BufferedImage bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "field_110560_d", "bufferedImage");
                    if (bufferedImage != null) {
                        PartsData partsData = new PartsData(uuid);
                        partsData.setPartInfo(PartsData.PartType.TAIL, TextureHelper.buildPartInfoFromSkin(uuid, PartsData.PartType.TAIL, bufferedImage));
                        Tails.proxy.addPartsData(uuid, partsData);
                    }
                }
            }
        }
    }

    public static class MiniPlayerRenderHelper implements IRenderHelper {

        @Override
        public void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z) {
            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-entity.renderYawOffset, 0F, 1F, 0F);
            if (!((EntityMiniPlayer) entity).isSitting()) GL11.glTranslatef(0F, 0.4F, -0.05F);
            else GL11.glTranslatef(0F, 0.1F, -0.05F);
            GL11.glScalef(0.35F, 0.35F, 0.35F);
            GL11.glRotatef(180F, 1F, 0F, 0F);
        }
    }*/

}
