-- BrainAlpha Database Schema

-- Table: users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Table: games
CREATE TABLE games (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    description TEXT,
    category TEXT
);

-- Table: user_progress
CREATE TABLE user_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    score BIGINT DEFAULT 0,
    last_played TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Table: game_sessions
CREATE TABLE game_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    score BIGINT DEFAULT 0,
    session_duration BIGINT DEFAULT 0,
    played_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Table: achievements
CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    description TEXT,
    game_id UUID REFERENCES games(id) ON DELETE CASCADE
);

-- Table: user_achievements
CREATE TABLE user_achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID REFERENCES achievements(id) ON DELETE CASCADE,
    unlocked_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Table: leaderboard
CREATE TABLE leaderboard (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    score BIGINT DEFAULT 0,
    rank BIGINT DEFAULT 0
);

-- Table: settings
CREATE TABLE settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    dark_mode BOOLEAN DEFAULT false,
    language TEXT DEFAULT 'en'
);
