package ws.zettabyte.oldowanobsidian.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class DamageSourcePure extends DamageSource {

	//Ignores all defense
	public DamageSourcePure() {
		super("pure");
		this.setDamageBypassesArmor();
	}
	@Override
	public IChatComponent func_151519_b (EntityLivingBase par1EntityLivingBase)
	{
		return new ChatComponentTranslation("death.attack.Pure", par1EntityLivingBase.func_145748_c_());
	}
	@Override
	public boolean isUnblockable()
	{
		return true;
	}
	@Override
	public boolean isDifficultyScaled()
	{
		return false;
	}
}
