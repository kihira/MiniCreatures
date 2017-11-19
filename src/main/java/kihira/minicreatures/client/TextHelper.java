package kihira.minicreatures.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TextHelper {

    public static void drawWrappedMessageFacingPlayer(double x, double y, double z, int maxWidth, double xOffset, String text) {
        drawWrappedMessageFacingPlayer(x, y, z, maxWidth, xOffset, text, 0.016666668F);
    }

    public static void drawWrappedMessageFacingPlayer(double x, double y, double z, int maxWidth, double xOffset, String text, double scale) {
        drawWrappedMessageFacingPlayer(x, y, z, maxWidth, xOffset, text, scale, -1);
    }

    /**
     * Renders a message wrapped to a certain length, expanding downwards
     * @param x:[[Double]] The x position
     * @param y:[[Double]] The y position
     * @param z:[[Double]] The z position
     * @param scale:[[Float]] The scale of the text
     * @param maxWidth:[[Int]] The maximum width of the string per line
     * @param xOffset:[[Double]] How far offset the chat should be from the position defined
     * @param text:[[Array[String]] An array of the string that is rendered in order
     * @param colour:[[Int]] The Minecraft version of colour
     */
    public static void drawWrappedMessageFacingPlayer(double x, double y, double z, int maxWidth, double xOffset, String text, double scale, int colour) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        double height = fontrenderer.listFormattedStringToWidth(text, maxWidth).size() * fontrenderer.FONT_HEIGHT;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(xOffset - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F);
        bufferBuilder.pos(xOffset - 1, height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F);
        bufferBuilder.pos(xOffset + maxWidth + 1, height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F);
        bufferBuilder.pos(xOffset + maxWidth + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F);
        Tessellator.getInstance().draw();

        GlStateManager.enableTexture2D();
        GlStateManager.translate(0F, 0F, -0.1F); //Move render forward a little to prevent flickering
        fontrenderer.drawSplitString(text, (int) xOffset, 0, maxWidth, colour);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawMultiLineMessageFacingPlayer(double x, double y, double z, String[] text) {
        drawMultiLineMessageFacingPlayer(x, y, z, text, 0.016666668F, -1, true, true);
    }

    /**
     * Draws a multi line string from the y position and expanding up.
     * @param x:[[Double]] The x position
     * @param y:[[Double]] The y position
     * @param z:[[Double]] The z position
     * @param scale:[[Float]] The scale of the text
     * @param text:[[Array[String]] An array of the string that is rendered in order
     * @param colour:[[Int]] The Minecraft version of colour
     * @param center:[[Boolean]] Whether to draw the string centered or left aligned
     * @param background:[[Boolean]] Whether to render the faint black background
     */
    public static void drawMultiLineMessageFacingPlayer(double x, double y, double z, String[] text, float scale, int colour, boolean center, boolean background) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        double height = text.length * fontrenderer.FONT_HEIGHT;
        int width = 0;
        for (String line : text) {
            int w = fontrenderer.getStringWidth(line);
            if (w > width) width = w;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        if (background) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            if (center) {
                //We add the offset of the font height to move it to the correct position
                bufferBuilder.pos(-(width / 2) - 1, fontrenderer.FONT_HEIGHT, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos(-(width / 2) - 1, -height + fontrenderer.FONT_HEIGHT - 1, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos((width / 2) + 1, -height + fontrenderer.FONT_HEIGHT - 1, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos((width / 2) + 1, fontrenderer.FONT_HEIGHT, 0.0D).color(0F, 0F, 0F, 0.25F);
            }
            else {
                bufferBuilder.pos(-1, height / 2, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos(-1, -(height / 2) - 1, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos(width + 1, -(height / 2) - 1, 0.0D).color(0F, 0F, 0F, 0.25F);
                bufferBuilder.pos(width + 1, height / 2, 0.0D).color(0F, 0F, 0F, 0.25F);
            }
            Tessellator.getInstance().draw();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.translate(0F, 0F, -0.1F); //Move render forward a little to prevent flickering
        for (String line : text) {
            if (center) fontrenderer.drawString(line, -fontrenderer.getStringWidth(line) / 2, 0, colour);
            else fontrenderer.drawString(line, 0, 0, colour);
            GlStateManager.translate(0F, -fontrenderer.FONT_HEIGHT, 0F);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }
}
