package kihira.minicreatures.client.gui;

import com.google.common.base.Strings;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.customizer.CustomizerRegistry;
import kihira.minicreatures.common.customizer.EnumPartCategory;
import kihira.minicreatures.common.entity.IMiniCreature;
import kihira.minicreatures.common.network.MiniCreaturesMessage;
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
    private int xSize = 205;
    private int ySize = 170;
    private int currentPage = 0;
    private GuiButton categoryButton;
    private EnumPartCategory currentCategory = EnumPartCategory.ALL;
    private String[] partsList = new String[6];
    private ArrayList<String> currentValidParts;
    private ArrayList<String> currentEquippedParts;
    //This should never be changed except during init
    private ArrayList<String> originalParts;

    public GuiCustomizer(IMiniCreature entity) {
        this.miniCreature = entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        this.buttonList.clear();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        //Add in category button
        this.categoryButton = new GuiButton(0, this.guiLeft + 83, this.guiTop + 7, 112, 20, StatCollector.translateToLocal("category." + this.currentCategory.name() + ".part"));
        this.buttonList.add(0, this.categoryButton);

        //Add in parts buttons
        for (int i = 1; i < 7; i++) {
            this.buttonList.add(i, new GuiButton(i, this.guiLeft + 83, this.guiTop + 10 + (i * 20), 92, 20, ""));
        }

        //Add in navigation buttons
        this.buttonList.add(7, new GuiButton(7, this.guiLeft + 86, this.height / 2 + 54, 20, 20, "<"));
        this.buttonList.add(8, new GuiButton(8, this.guiLeft + 172, this.height / 2 + 54, 20, 20, ">"));
        this.buttonList.add(9, new GuiButton(9, this.guiLeft + 115, this.height / 2 + 54, 49, 20, StatCollector.translateToLocal("gui.done")));

        //Load current part data.
        this.currentEquippedParts = this.miniCreature.getCurrentParts();
        this.originalParts = new ArrayList<String>(this.currentEquippedParts);

        //Always perform this last
        updatePartsList();
        updateNavButtons();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void actionPerformed(GuiButton button) {
        //Category button
        if (button.id == 0) {
            EnumPartCategory[] partCategories = this.miniCreature.getPartCatergoies().toArray(new EnumPartCategory[20]);
            if (this.currentCategory.ordinal() + 1 >= this.miniCreature.getPartCatergoies().size()) this.currentCategory = EnumPartCategory.ALL;
            else this.currentCategory = partCategories[this.currentCategory.ordinal() + 1];
            this.categoryButton.displayString = StatCollector.translateToLocal("category." + this.currentCategory.name() + ".part");
        }
        //Nav button
        else if (button.id == 7) this.currentPage--;
        else if (button.id == 8) this.currentPage++;
        else if (button.id == 9) closeGUI(true);
        //Parts buttons
        if (button.id > 0 && button.id < 7) {
            String partName = this.partsList[button.id - 1];
            if (this.currentEquippedParts.contains(partName)) this.currentEquippedParts.remove(partName);
            else this.currentEquippedParts.add(partName);
        }

        //Update everything just to be safe
        updatePartsList();
        updateNavButtons();
    }

    private void updateNavButtons() {
        ((GuiButton) this.buttonList.get(7)).enabled = this.currentPage != 0;
        ((GuiButton) this.buttonList.get(8)).enabled = !((this.currentPage + 1) * 6 >= this.currentValidParts.size());
    }

    private void updatePartsList() {
        this.currentValidParts = CustomizerRegistry.getValidParts(this.miniCreature, this.currentCategory);
        this.partsList = new String[6];
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

    private void closeGUI(boolean shouldUpdate) {
        if (shouldUpdate) {
            MiniCreatures.packetHandler.sendToServer(new MiniCreaturesMessage.UpdateEntityMessage(this.miniCreature.getEntity().getEntityId(), this.currentEquippedParts));
        }
        else {
            this.miniCreature.setParts(this.originalParts);
        }
        this.mc.displayGuiScreen(null);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == 1) closeGUI(false);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawBackground(par1, par2);
        drawForeground(par1, par2);
        super.drawScreen(par1, par2, par3);
    }

    private void drawForeground(int p_146979_1_, int p_146979_2_) {
        this.mc.getTextureManager().bindTexture(this.guiTextures);
        //Render checkmarks if part is currently equipped
        for (int i = 0; i < 6; i++) {
            if (!Strings.isNullOrEmpty(this.partsList[i])) {
                if (this.currentEquippedParts.contains(this.partsList[i])) this.drawTexturedModalRect(this.guiLeft + 174, this.guiTop + 10 + ((i + 1) * 20), 0, 168, 20, 20);
                else this.drawTexturedModalRect(this.guiLeft + 174, this.guiTop + 10 + ((i + 1) * 20), 22, 168, 20, 20);
            }
        }
    }

    private void drawBackground(int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.guiTextures);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        GuiInventory.func_147046_a(this.guiLeft + 42, this.guiTop + 82, 45, (float) (this.guiLeft + 51) - p_146976_2_, (float) (this.guiTop + 75 - 50) - p_146976_3_, this.miniCreature.getEntity());
    }
}
