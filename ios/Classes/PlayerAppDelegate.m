//
//  PlayerAppDelegate.m
//  Player
//
//  Created by Gleb Gadyatskiy on 21/05/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import "PlayerAppDelegate.h"
#import "PlayerViewController.h"
#include <AudioToolbox/AudioToolbox.h>
#import "GBMusicTrack.h"
@implementation PlayerAppDelegate

@synthesize window;
@synthesize viewController;
@synthesize webView;




- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    
    // Override point for customization after app launch    
    [window addSubview:viewController.view];
	
	// Example 1, loading the content from a URLNSURL
	//NSURL *url = [NSURL URLWithString:@"http://google.com"];
	//NSURLRequest *request = [NSURLRequest requestWithURL:url];
	//[webView loadRequest:request];
	
	NSString *htmlFile = [[NSBundle mainBundle] pathForResource:@"main" ofType:@"html"];
	NSData *htmlData = [NSData dataWithContentsOfFile:htmlFile];
	[webView loadData:htmlData MIMEType:@"text/html" textEncodingName:@"UTF-8" baseURL:[NSURL fileURLWithPath:[[NSBundle mainBundle] bundlePath]]];
    
    [window makeKeyAndVisible];
	//NSLog(@"Start MP3");	
	//GBMusicTrack *song = [[GBMusicTrack alloc] initWithPath:[[NSBundle mainBundle] pathForResource:@"gorun" ofType:@"mp3"]];
	//[song setRepeat:YES];
	//[song play];
	//NSLog(@"End MP3");
}

- (void)dealloc {
    [viewController release];
    [window release];
	[webView release];
    [super dealloc];
}


@end

