CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--guardar uma tabela "views" pra trackear as views dos posts ia dar mt merda pq ia ter bilhoes de 
--entradas ent qnd um usuario carrega um post ele so soma um numero de views e pronto. nos stories
--e diferente pq geralmente n tem mt view/story

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    email VARCHAR(128) UNIQUE NOT NULL,
    username VARCHAR(32) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    is_private BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users_status (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    is_online BOOLEAN DEFAULT TRUE,
    is_suspended BOOLEAN DEFAULT FALSE,
    is_banned BOOLEAN DEFAULT FALSE,
    timeout_end TIMESTAMP WITH TIME ZONE,
    timeout_message TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE follows (
    follower_id UUID REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pending', -- pending accepted ou rejected
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id)
);

CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    description TEXT,
    comments_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    impressions BIGINT DEFAULT 0
);

CREATE TABLE post_media (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    post_id UUID REFERENCES posts(id) ON DELETE CASCADE,
    media_type VARCHAR(10) NOT NULL, -- image ou video
    media_path TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stories (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    media_type VARCHAR(10) NOT NULL, -- image ou video
    media_path TEXT NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE story_views (
    story_id UUID REFERENCES stories(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    viewed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (story_id, user_id)
);

CREATE TABLE likes (
    id UUID PRIMARY KEY DEFAULT generate_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    target_type VARCHAR(20) NOT NULL, -- post comment ou story
    target_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, target_type, target_id)
);

-- se o usuario se comunicasse com o bd usando o uuid diretamente
-- qualquer um poderia fazer uma request com qualquer uuid, e se for
-- o de alguem o cara mal ta praticamente logado na conta da vitima
-- pra sempre. pra resolver isso, qnd vc loga vc gera uma sessao e um id
-- aleatorio, e vc se comunica com o bd usando esse id da sessao, ai o 
-- banco sabe quem vc e por meio dessa tabela aqui.
-- um cara mal ainda consegue forjar um id de sessao e entrar numa 
-- conta aleatoria (ou mais comumente roubar um id de sessao) mas ai
-- se vc terminar sua sessao vc gera outro id e o do cara n
-- funciona mais
CREATE TABLE sessions ( 
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expired BOOLEAN DEFAULT FALSE
);

