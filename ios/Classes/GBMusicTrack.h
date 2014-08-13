#ifndef __GBMUSICTRACK_H
#define __GBMUSICTRACK_H

//http://www.idevgames.com/forum/showthread.php?t=15280
#import <AudioToolbox/AudioQueue.h>
#import <AudioToolbox/AudioFile.h>
#import "UIWebViewDelegate.h"


#define NUM_QUEUE_BUFFERS	3

typedef enum {
	EAudioStateClosed,
	EAudioStateStopped,
	EAudioStatePlaying,
	EAudioStatePaused,
	EAudioStateSeeking
} EAudioState;

@interface GBMusicTrack : NSObject
{
	AudioFileID						audioFile;
	AudioStreamBasicDescription		dataFormat;
	AudioQueueRef					queue;
	UInt64							packetIndex;
	UInt32							numPacketsToRead;
	AudioStreamPacketDescription*	packetDescs;
	BOOL							repeat;
	BOOL							trackClosed;
	AudioQueueBufferRef				buffers[NUM_QUEUE_BUFFERS];
	EAudioState						audioState;
	UIWebView*						resumeAfter;
}

- (id)initWithPath:(NSString *)path;
- (void)setGain:(Float32)gain;
- (void)setRepeat:(BOOL)yn;
- (void)setResume:(UIWebView*)webView;
- (void)play;
- (void)pause;
- (void)stop;
- (void)seek:(UInt64)packetOffset;
- (EAudioState)getState;


// close is called automatically in GBMusicTrack's dealloc method, but it is recommended
// to call close first, so that the associated Audio Queue is released immediately, instead
// of having to wait for a possible autorelease, which may cause some conflict
- (void)close;

extern NSString* GBMusicTrackFinishedPlayingNotification;

@end

#endif