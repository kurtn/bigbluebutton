package org.bigbluebutton.live.models

case class MeetingInfo (
	val name: String,
	val externalID: String,
	val internalID: String,	
	val duration: Long,
	val createdOn: Long,
	val startedOn: Long,
	val endedOn: Long, 
	val telVoice: String,
	val webVoice: String,
	val moderatorPass: String,
	val viewerPass: String, 
	val welcomeMsg: String,
	val logoutUrl: String,
	val maxUsers: Number,
	val record: Boolean,
	val dialNumber: String,
	val defaultAvatarURL: String
)