package kihira.minicreatures.client.gui;

import kihira.minicreatures.common.CustomizerRegistry;
import kihira.minicreatures.common.entity.IMiniCreature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GuiCustomizer extends GuiScreen {

    private final IMiniCreature miniCreature;
    private final ResourceLocation guiTextures = new ResourceLocation("minicreatures", "textures/gui/customizer.png");
    private int guiLeft;
    private int guiTop;
    private int xSize = 180;
    private int ySize = 170;
    private int currentPage = 0;
    private GuiButton categoryButton;
    private CustomizerRegistry.EnumPartCategory currentCategory = CustomizerRegistry.EnumPartCategory.ALL;
    private String[] partsList = new String[6];
    private ArrayList<String> currentValidParts;

    public GuiCustomizer(IMiniCreature entity) {
        this.miniCreature = entity;
        this.allowUserInput = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Add in category button
        this.categoryButton = new GuiButton(0, this.guiLeft + 82, this.guiTop + 7, 92, 20, StatCollector.translateToLocal("category." + this.currentCategory.name() + ".part"));
        this.buttonList.add(0, this.categoryButton);

        //Add in parts buttons
        for (int i = 1; i < 7; i++) {
            this.buttonList.add(i, new GuiButton(i, this.guiLeft + 82, this.guiTop + 10 + (i * 20), 92, 20, ""));
        }
        updatePartsList();

        //Add in navigation buttons
        this.buttonList.add(7, new GuiButton(7, this.guiLeft + 82, this.height / 2 + 54, 20, 20, "<"));
        this.buttonList.add(8, new GuiButton(8, this.guiLeft + 154, this.height / 2 + 54, 20, 20, ">"));
        this.buttonList.add(9, new GuiButton(9, this.guiLeft + 104, this.height / 2 + 54, 48, 20, StatCollector.translateToLocal("gui.done")));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void actionPerformed(GuiButton button) {
        //Category button
        if (button.id == 0) {
            CustomizerRegistry.EnumPartCategory[] partCategories = this.miniCreature.getPartCatergoies().toArray(new CustomizerRegistry.EnumPartCategory[20]);
            if (this.currentCategory.ordinal() + 1 >= this.miniCreature.getPartCatergoies().size()) this.currentCategory = CustomizerRegistry.EnumPartCategory.ALL;
            else this.currentCategory = partCategories[this.currentCategory.ordinal() + 1];
            this.categoryButton.displayString = StatCollector.translateToLocal("category." + this.currentCategory.name() + ".part");
        }
        //Nav button
        else if (button.id == 7) this.currentPage--;
        else if (button.id == 9) this.currentPage++;

        //Parts buttons
        if (button.id > 0 && button.id < 7) {

        }

        //Update everything just to be safe
        updatePartsList();
        updateNavButtons();
    }

    private void updateNavButtons() {
        ((GuiButton) this.buttonList.get(7)).enabled = this.currentPage != 0;
        ((GuiButton) this.buttonList.get(9)).enabled = !((this.currentPage + 1) * 6 >= this.currentValidParts.size());
    }

    private void updatePartsList() {
        this.currentValidParts = CustomizerRegistry.getValidParts(this.miniCreature, this.currentCategory);
        for (int i = 1; i < 7; i++) {
            int num = (this.currentPage * 6) + i - 1;
            GuiButton button = (GuiButton) this.buttonList.get(i);
            if (this.currentValidParts.size() > num) {
                button.visible = true;
                button.displayString = StatCollector.translateToLocal("name." + this.currentValidParts.get(num) + ".part");
                this.partsList[num] = this.currentValidParts.get(num);
            }
            else {
                button.visible = false;
            }
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawBackground(par3, par1, par2);
        super.drawScreen(par1, par2, par3);
    }

    protected void drawForeground(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj.drawString(this.miniCreature.getEntity().getCommandSenderName(), 8, 6, 4210752);
    }

    protected void drawBackground(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.guiTextures);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GuiInventory.func_147046_a(this.guiLeft + 42, this.guiTop + 82, 45, (float) (this.guiLeft + 51) - p_146976_2_, (float) (this.guiTop + 75 - 50) - p_146976_3_, this.miniCreature.getEntity());
    }
}
