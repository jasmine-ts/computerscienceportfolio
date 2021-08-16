import pandas as pd
import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
import spotipy.util as util

scope = 'user-library-read playlist-modify-public playlist-read-private playlist-modify-private playlist-read-collaborative'
redirect_uri = 'https://www.google.com/'


def main():
	
	user_credentials = get_credentials()
	username = user_credentials['username']
	cid = user_credentials['client_id']
	secret = user_credentials['client_secret']

	client_credentials_manager = spotipy.oauth2.SpotifyOAuth(scope=scope, 
															username=username,
															client_id=cid,
															client_secret=secret,
															redirect_uri=redirect_uri)

	sp = spotipy.Spotify(client_credentials_manager=client_credentials_manager)

	print("""
		If you would like to filter your entire saved song library, input \"Liked Songs\".
		Otherwise, input the name of the playlist you would like to filter below.
		""")


	# User inputs requested playlist (or all liked songs) to filter
	requested_playlist_name = input("Name of playlist you would like to filter: ").strip()
	
	# Get the tracklist to filter based on user input
	if requested_playlist_name.lower() == "liked songs":
		tracklist = get_all_saved_tracks(sp)
	else:
		user_playlists = get_all_user_playlists(sp)
		playlist = find_playlist(user_playlists, requested_playlist_name)
		tracklist = get_playlist_tracks(sp, playlist)

	# Print an info block on different bpm ranges
	print_bpm_ranges()

	# User inputs requested minimum and maximum tempos to filter by
	requested_tempo_min = int(input("Minimum Tempo: ").strip())
	requested_tempo_max = int(input("Maximum Tempo: ").strip())
	tempo_range = [requested_tempo_min,  requested_tempo_max]

	# Given tracklist and tempo_range, filter accordingly
	filtered_tracks = process_tracklist(sp, tracklist, tempo_range)

	# Create new playlist with user input name, then add filtered tracks
	new_playlist_name = input("Name of your new playlist: ").strip()
	create_playlist(sp, username, filtered_tracks, new_playlist_name, tempo_range)


def get_all_user_playlists(sp):
	offset = 0
	playlists = []

	while True:
		next_playlists = sp.current_user_playlists(limit=50, offset=offset)
		playlists += next_playlists['items']
		if next_playlists['next'] is not None:
			offset += 50
		else:
			break

	return playlists

def find_playlist(playlists, name):
	for pl in playlists:
		pl_name = pl['name'].lower()
		if pl_name == name.lower():
			return pl
	print("No such playlist named \"" + name + "\"")
	quit()

def get_playlist_tracks(sp, playlist):
	return sp.playlist_items(playlist['id'])['items']

def get_all_saved_tracks(sp):
	offset = 0
	tracks = []

	while True:
		content = sp.current_user_saved_tracks(limit=50, offset=offset)
		tracks += content['items']
		if content['next'] is not None:
			offset += 50
		else:
			break
	return tracks

def process_tracklist(sp, tracklist, tempo_range):
	# process tracklist into a pandas dataframe with tempo attributes indexed by track id
	names = []
	ids = []
	for track in tracklist:
		track_name = track['track']['name']
		track_id = track['track']['id']
		if track_name is not None and track_id is not None:
			names.append(track_name)
			ids.append(track_id)
	index = 0
	audio_features = []


	while index < len(ids):
		audio_features += sp.audio_features(ids[index:index + 50])
		index += 50

	features_list = []

	for features in audio_features:
		features_list.append([features['tempo']])

	df = pd.DataFrame(features_list, columns=['tempo'], index=ids)

	# create new dataframe with songs that lie within requested tempo range
	min_tempo = tempo_range[0]
	max_tempo = tempo_range[1]

	new_df = df.loc[(df['tempo'] >= min_tempo) & (df['tempo'] <= max_tempo)]

	# return a list of song ids that belong to the new dataframe
	return new_df.index.to_list()

def create_playlist(sp, username, tracklist, playlist_name, tempo_range):
	min_tempo = tempo_range[0]
	max_tempo = tempo_range[1]
	playlist = sp.user_playlist_create(username, playlist_name, public=True, collaborative=False, description=('Tempo Range: ' + str(min_tempo) + '-' + str(max_tempo) + ' BPM'))
	
	i = 0
	offset = 99
	tracklist_length = len(tracklist)
	while i < tracklist_length - 1:
		sp.user_playlist_add_tracks(username, playlist['id'], tracklist[i : i + offset])

		if tracklist_length - i < 99:
			i += tracklist_length - i - 1
		else:
			i += offset

	print("Success! You can find your newly created playlist in Spotify.")

def print_bpm_ranges():
	ranges = """
	Dub: 60-90 bpm
	Hip-hop: 60-100 bpm
	House: 115-130 bpm
	Techno/trance: 120-140 bpm
	Dubstep: 135-145 bpm
	Drum and bass: 160-180 bpm
	"""
	print(ranges)

def get_credentials():
	info_dict = {}
	with open("config.txt", "r") as file:
		content = file.readlines()
		for line in content:
			if "=" in line:
				parts = line.split("=")
				if len(parts) == 2:
					info_dict[parts[0].strip()] = parts[1].strip()
				else:
					print("Please fill out the config.txt file")
					exit()
		return info_dict

main()
