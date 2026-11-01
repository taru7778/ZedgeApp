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
// tarek - 4 accounts
// ---------------------------------------------------------------------------
const BuildProfile kProfileTarek = BuildProfile(
  id: 'tarek',
  title: 'Tarek (4 accounts)',
  r2WorkerUrl: 'https://tarek.henrydelacruz0t7.workers.dev',
  googleCalendarApiKey: 'AIzaSyAnNeYfEYF6Z41r-QBo2q8eWKaP-CBPlnc',
  defaultUploadWindows: {
    'zedge1': [4, 10, 16],
    'zedge2': [5, 11, 17],
    'zedge3': [10, 16, 20],
    'zedge4': [11, 17, 21],
  },
  accounts: [
    FirebaseAccountConfig(
      key: 'zedge1',
      apiKey: '',
      authDomain: 'zedge-1.firebaseapp.com',
      databaseURL: 'https://zedge-1-default-rtdb.firebaseio.com',
      projectId: 'zedge-1',
      storageBucket: 'zedge-1.firebasestorage.app',
      messagingSenderId: '',
      appId: '',
    ),
    FirebaseAccountConfig(
      key: 'zedge2',
      apiKey: '',
      authDomain: 'zedge2-34d95.firebaseapp.com',
      databaseURL: 'https://zedge2-34d95-default-rtdb.firebaseio.com',
      projectId: 'zedge2-34d95',
      storageBucket: 'zedge2-34d95.firebasestorage.app',
      messagingSenderId: '',
      appId: '',
    ),
    FirebaseAccountConfig(
      key: 'zedge3',
      apiKey: '',
      authDomain: 'zedge3-1b3dc.firebaseapp.com',
      databaseURL: 'https://zedge3-1b3dc-default-rtdb.firebaseio.com',
      projectId: 'zedge3-1b3dc',
      storageBucket: 'zedge3-1b3dc.firebasestorage.app',
      messagingSenderId: '',
      appId: '',
    ),
    FirebaseAccountConfig(
      key: 'zedge4',
      apiKey: '',
      authDomain: 'zedge4-3bf74.firebaseapp.com',
      databaseURL: 'https://zedge4-3bf74-default-rtdb.asia-southeast1.firebasedatabase.app',
      projectId: 'zedge4-3bf74',
      storageBucket: 'zedge4-3bf74.firebasestorage.app',
      messagingSenderId: '',
      appId: '',
    ),
  ],
);
