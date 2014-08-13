//
//  UIWebViewDelegate.m
//  Player
//
//  Created by Gleb Gadyatskiy on 13/07/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//

#import "UIWebViewDelegate.h"
#import "GBMusicTrack.h"

@implementation UIWebViewDelegate

GBMusicTrack *sound;
int position;

- (BOOL)webView:(UIWebView*)webView shouldStartLoadWithRequest:(NSURLRequest*)request navigationType:(UIWebViewNavigationType)navigationType {
	//NSLog(@"request");
	NSString* urlString = [[request URL] absoluteString];
	
	if ([urlString hasPrefix:@"command:"]) {
		NSString* command = [urlString substringFromIndex:10 ];
		NSString* path = [[request URL] path];

		NSArray* pathComponents = [path pathComponents];
		if ([pathComponents count]>1) {
			int pos = [pathComponents count]-1;
			NSString* ext = [pathComponents objectAtIndex:pos--];
			NSString* file = [pathComponents objectAtIndex:pos];
			if ([command hasPrefix:@"sound/"]) { 
				if (sound != nil) {
					[sound setResume:nil];
					[sound close];
					[sound dealloc];
				}
				sound = [[GBMusicTrack alloc] initWithPath:[[NSBundle mainBundle] pathForResource:file ofType:ext]];
				[sound setRepeat:NO];
				[sound play];
			} else if ([command hasPrefix:@"music/"]) { 
				if (sound != nil) {
					[sound setResume:nil];
					[sound close];
					[sound dealloc];
				}
				sound = [[GBMusicTrack alloc] initWithPath:[[NSBundle mainBundle] pathForResource:file ofType:ext]];
				[sound setRepeat:YES];
				//[sound setResume:webView];
				[sound play];
			} else if ([command hasPrefix:@"state/"]) { 
				//NSLog(@"save state");
				NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
				// saving a string
				[prefs setObject:ext forKey:@"state"];
				// saving it all
				[prefs synchronize];
			} else if ([command hasPrefix:@"url/"]) { 
				NSString* url = [path substringFromIndex:1 ];
				[[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]]; 
			}
		} else if ([command isEqualToString:@"stopSound"] || [command isEqualToString:@"stopMusic"]) { 
			if (sound != nil) {
				[sound setResume:nil];
				[sound close];
				[sound dealloc];
				sound = nil;
			}
		} else if ([command isEqualToString:@"restore"]) { 
			//NSLog(@"restore state");
			NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
			// getting the string
			NSString* state = [prefs stringForKey:@"state"];
			//NSLog([webView stringByEvaluatingJavaScriptFromString:@"document.title"]); 
			NSString *jsCommand;
			if (state==nil) {
				jsCommand = @"window.noGameState();";
			} else {
				jsCommand = [NSString stringWithFormat:@"window.restoreGameState('%@');", state];
			}
			[webView stringByEvaluatingJavaScriptFromString:jsCommand]; 
			//NSLog(reply);
		}
		return NO;
	} else {
		return YES;
	}
}

- (void)dealloc {
    [super dealloc];
}
					


@end
