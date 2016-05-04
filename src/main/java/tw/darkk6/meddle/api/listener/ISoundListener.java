package tw.darkk6.meddle.api.listener;

import tw.darkk6.meddle.api.event.SoundEvent;

public interface ISoundListener extends IEventListener {
	void onSoundPlay(SoundEvent e);
}
