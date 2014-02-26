package kihira.minicreatures.common.customizer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public interface ICustomizerPart {

    public boolean isPartValidForEntity(Entity entity, EnumPartCategory enumPartCategory);

    /**
     * These are the same parameters that are passed to render in ModelBase with the addition of the model being rendered
     */
    public void render(Entity entity, ModelBase model, float par2, float par3, float par4, float par5, float par6, float par7);
}
