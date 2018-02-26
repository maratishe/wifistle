
This is an Android application for testing under the WiFistle project. 

*The objective of the project:* local/indoors positioning using WiFi and ultrasound. 

*Constraints:*  Only smartphone-level software. The logic should be distributed but basically work the same for each individual participant. 


The basic idea is:
1. Encode WiFi SSIDs with numbers (hashkeys) so that users are sufficiently unique (collissions are still possible) 
2. Use prefix of SSIDs (2 letters = 1 byte = 256 unique numbers) for identifiers. 
3. Convert the SSID prefix into an ultrasound tone. The Whistle.java class and whistle() API in the main Wifistle.json does basic tone generation. Supports up to 10 separate tones in the mix.  Tests show that multiple freqs mix just fine.
4. Other users, when hear the (mixed) tone, should be able to convert it back to SSID IDs (from the other user) and compare then to those that he/she sees locally.  WiFi reach is larger than ultrasound, so, up to a given distance the ID lists should be roughly the same.  The project aims at multidimensional positioning based on the *differences* between the lists. 


The basic approach to positioning:
1. Based on the idea that "interference benefits networking coding while other transmission techniques degrade". 
2. The tone is a coded message, where each SSID prefix is a code word.  The differences in the mixes for different users offers (a very vague so far) possibility to convert the diff into *relative angle of rotation* for each user.   Once accomplished, it enables each user to tell nearby users apart.  This breaks from trandition, in which users on both sides on the circle (sound/WiFi reach) would otherwise appear the same, since delay measurement only provides one-dimensional data. 


The software only provides components and will not work out of the box.  It is supposed to poll the API (the screen shows the IP:port) which would provide commands such as *whistle*, *spectrum*, etc.  There are other useful copmonents there as well (G-sensor, WiFi scan, etc.).  The *Hear.java* class polls raw byte from the mic.  *Spectrum.java* performs FFT on the fly and can actually *understand* the tone mix by detecting each individual frequency in the tone.  All these classes have been tested in both quiet and noisy conditions and have been proved to work. 

This is a simple description, for now. 