package org.bigbluebutton.conference.service.recorder;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDispatcher implements Recorder {

	private static final String COLON=":";
	JedisPool redisPool;
	
	public RedisDispatcher(){
		super();

	}
	
	@Override
	public void record(String session, RecordEvent message) {		
		Jedis jedis = redisPool.getResource();
		try {
			Long msgid = jedis.incr("global:nextRecordedMsgId");
			jedis.hmset("recording" + COLON + session + COLON + msgid, message.toMap());
			jedis.rpush("meeting" + COLON + session + COLON + "recordings", msgid.toString());
		} finally {
			redisPool.returnResource(jedis);
		}						
	}
	
	public JedisPool getRedisPool() {
		return redisPool;
	}

	public void setRedisPool(JedisPool redisPool) {
		this.redisPool = redisPool;
	}
	

}
