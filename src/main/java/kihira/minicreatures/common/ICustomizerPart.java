package kihira.minicreatures.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;

public interface ICustomizerPart<MODEL extends ModelBase> {

    public boolean isPartValidForEntity(Entity entity);

    /**
     * These are the same parameters that are passed to render in ModelBase with the addition of the model being rendered
     */
    public void render(Entity entity, MODEL model, float par2, float par3, float par4, float par5, float par6, float par7);
}
