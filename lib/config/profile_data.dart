// Build profiles available in this copy of the app.
//
// This bundle ships with its own profile only (values mirror the top of index.html).
// Add more profiles here and pick one with `--dart-define=BUILD=<id>`.

import 'build_config.dart';

/// Profile used when the app is built without `--dart-define=BUILD=...`.
const String kDefaultProfileId = 'tarek';

/// Every profile compiled into this executable.
const List<BuildProfile> kProfiles = [
  kProfileTarek,
];

// ---------------------------------------------------------------------------
// tarek (tarek-automation.zip) - 3 accounts
// ---------------------------------------------------------------------------
const BuildProfile kProfileTarek = BuildProfile(
  id: 'tarek',
  title: 'Tarek (3 accounts)',
  r2WorkerUrl: 'https://tarek.henrydelacruz0t7.workers.dev',
  googleCalendarApiKey: '',
  defaultUploadWindows: {
    'zedge1': [10, 15, 20],
    'zedge2': [11, 16, 21],
    'zedge3': [5, 11, 17],
  },
  telegramNote: 'Telegram bot token + chat id 8998798162 baked into zedge1-3.yml (greeting "হ্যালো তারেক ভাই").',
  accounts: [
    FirebaseAccountConfig(
      key: 'zedge1',
      apiKey: 'AIzaSyANCi1hk-QCPrEHqssN0K14HEfRuQOuLPM',
      authDomain: 'zedge-1.firebaseapp.com',
      databaseURL: 'https://zedge-1-default-rtdb.firebaseio.com',
      projectId: 'zedge-1',
      storageBucket: 'zedge-1.firebasestorage.app',
      messagingSenderId: '1084896886034',
      appId: '1:1084896886034:web:ff81b89bb64306dc1207cb',
    ),
    FirebaseAccountConfig(
      key: 'zedge2',
      apiKey: 'AIzaSyC8tIjsjsmqE_vCxbuKXUrJLcYDKpceBjs',
      authDomain: 'zedge2-34d95.firebaseapp.com',
      databaseURL: 'https://zedge2-34d95-default-rtdb.firebaseio.com',
      projectId: 'zedge2-34d95',
      storageBucket: 'zedge2-34d95.firebasestorage.app',
      messagingSenderId: '185050219647',
      appId: '1:185050219647:web:94a9a545ccd2974d7b6b4a',
    ),
    FirebaseAccountConfig(
      key: 'zedge3',
      apiKey: 'AIzaSyAQhJVx-f0fBoFs1a6zJoCvygy4EXVOkaE',
      authDomain: 'zedge3-1b3dc.firebaseapp.com',
      databaseURL: 'https://zedge3-1b3dc-default-rtdb.firebaseio.com',
      projectId: 'zedge3-1b3dc',
      storageBucket: 'zedge3-1b3dc.firebasestorage.app',
      messagingSenderId: '276111856105',
      appId: '1:276111856105:web:9eba089a75ef6f6fe1bb78',
    ),
  ],
);
