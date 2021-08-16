# Filter By Tempo
This program allows you to generate new playlists filtered by tempo
from your existing Spotify playlists or Liked Songs library.

## What You Will Need
- Spotify account
- Spotify Client ID (see below)
- Spotify Client Secret (see below)

## Set Up
1. Get your Spotify Client ID and Client Secret, and set your Redirect URI
	- Visit https://developer.spotify.com/dashboard/
	- Create an app
	- Access your Client ID and Client Secret in your app overview page
	- Press edit settings, then add https://www.google.com/ as the Redirect URI

2. Download and fill out config.txt
	- Open config.txt
	- Paste your Username, Client ID, and Client Secret in their respective spots
	- Save the file

3. Download requirements.txt
	- After downloading, open terminal or command proompt
	- Go to the directory where the project is located
	- Run: `pip install -r requirements.txt` in the command line to install the necessary libraries

4. Sign in to Spotify in the Filter By Tempo app
	- Run `python tempo_filter.py` in terminal/command prompt
	- Sign into your Spotify account when prompted in browser
	- Respond to the app prompts in terminal/command prompt to filter and generate a new playlist