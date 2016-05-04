package tw.darkk6.meddle.api.listener;

public interface ITickListener extends IEventListener {
	void onTickStart();
	void onTickEnd();
}
