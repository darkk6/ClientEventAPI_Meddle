package tw.darkk6.meddle.api.listener;

import net.minecraft.client.gui.GuiScreen;

public interface IGuiOpenListener extends IEventListener {
	void onGuiOpen(GuiScreen gui);
}
