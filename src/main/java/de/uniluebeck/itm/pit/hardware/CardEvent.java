package de.uniluebeck.itm.pit.hardware;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class CardEvent implements Event
{
	private final EventTarget target;
	private final long timestamp;
	private final String type;
	
	public CardEvent(String type, EventTarget target)
	{
		this.type = type;
		this.target = target;
		this.timestamp = System.currentTimeMillis();
	}
	
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public EventTarget getTarget()
	{
		return target;
	}

	@Override
	public EventTarget getCurrentTarget()
	{
		return target;
	}

	@Override
	public short getEventPhase()
	{
		return 0;
	}

	@Override
	public boolean getBubbles()
	{
		return false;
	}

	@Override
	public boolean getCancelable()
	{
		return false;
	}

	@Override
	public long getTimeStamp()
	{
		return timestamp;
	}

	@Override
	public void stopPropagation()
	{
	}

	@Override
	public void preventDefault()
	{
	}

	@Override
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg)
	{
	}
}
