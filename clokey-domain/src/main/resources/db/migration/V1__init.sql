CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL,
                        nickname VARCHAR(30),
                        clokey_id VARCHAR(255) UNIQUE,

                        social_type VARCHAR(20) NOT NULL CHECK (
                            social_type IN ('KAKAO', 'APPLE')
                            ),

                        status VARCHAR(15) NOT NULL DEFAULT 'ACTIVE' CHECK (
                            status IN ('ACTIVE', 'INACTIVE')
                            ),

                        register_status VARCHAR(30) NOT NULL DEFAULT 'NOT_AGREED' CHECK (
                            register_status IN ('NOT_AGREED', 'AGREED_PROFILE_NOT_SET', 'REGISTERED')
                            ),

                        visibility VARCHAR(15) NOT NULL DEFAULT 'PUBLIC' CHECK (
                            visibility IN ('PUBLIC', 'PRIVATE')
                            ),

                        profile_image_url VARCHAR(255),
                        profile_back_image_url VARCHAR(255),
                        bio TEXT,
                        refresh_token VARCHAR(255),
                        access_token VARCHAR(255),
                        device_token VARCHAR(255),
                        apple_refresh_token VARCHAR(255),
                        kakao_id VARCHAR(255),

                        banned BOOLEAN NOT NULL DEFAULT FALSE,
                        inactive_date DATE,

                        created_at DATETIME(6) NOT NULL,
                        updated_at DATETIME(6) NOT NULL
);
CREATE INDEX idx_member_clokey_id ON member (clokey_id);


CREATE TABLE term (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,

                      title VARCHAR(255) NOT NULL,
                      body TEXT NOT NULL,
                      optional BOOLEAN NOT NULL,

                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6) NOT NULL
);


CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          parent_id BIGINT,
                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6) NOT NULL,

                          CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
);

CREATE TABLE folder (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        member_id BIGINT NOT NULL,
                        name VARCHAR(30) NOT NULL,
                        item_count BIGINT NOT NULL DEFAULT 0,
                        created_at DATETIME(6) NOT NULL,
                        updated_at DATETIME(6) NOT NULL,
                        CONSTRAINT fk_folder_member FOREIGN KEY (member_id) REFERENCES member(id)
);


CREATE TABLE cloth (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       cloth_image_url VARCHAR(255) NOT NULL ,
                       cloth_url VARCHAR(1000),
                       name VARCHAR(255),
                       price INT price,
                       brand VARCHAR(255),
                       category_id BIGINT NOT NULL,
                       member_id BIGINT NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,

                       CONSTRAINT fk_cloth_category FOREIGN KEY (category_id) REFERENCES category(id),
                       CONSTRAINT fk_cloth_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE history (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         history_date DATE NOT NULL,
                         likes INTEGER NOT NULL DEFAULT 0,
                         content VARCHAR(200),
                         banned BOOLEAN NOT NULL DEFAULT FALSE,
                         member_id BIGINT NOT NULL,
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL,

                         CONSTRAINT fk_history_member FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE INDEX idx_member_date ON history (member_id, history_date);

CREATE TABLE hashtag (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(30) NOT NULL UNIQUE,
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL
);


CREATE TABLE comment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         content VARCHAR(100) NOT NULL,
                         member_id BIGINT NOT NULL,
                         history_id BIGINT NOT NULL,
                         parent_id BIGINT,
                         banned BOOLEAN NOT NULL,
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL,
                         CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id),
                         CONSTRAINT fk_comment_history FOREIGN KEY (history_id) REFERENCES history(id),
                         CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id)
);

CREATE TABLE reply (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         content VARCHAR(100) NOT NULL,
                         banned BOOLEAN NOT NULL,
                         member_id BIGINT NOT NULL,
                         comment_id BIGINT NOT NULL,
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6) NOT NULL,
                         CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id),
                         CONSTRAINT fk_comment_comment FOREIGN KEY (comment_id) REFERENCES comment(id)
);



CREATE TABLE cloth_folder (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              cloth_id BIGINT NOT NULL,
                              folder_id BIGINT NOT NULL,
                              created_at DATETIME(6) NOT NULL,
                              updated_at DATETIME(6) NOT NULL,
                              CONSTRAINT fk_cloth_folder_cloth FOREIGN KEY (cloth_id) REFERENCES cloth(id),
                              CONSTRAINT fk_cloth_folder_folder FOREIGN KEY (folder_id) REFERENCES folder(id)
);



CREATE TABLE history_cloth (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               history_id BIGINT NOT NULL,
                               cloth_id BIGINT NOT NULL,
                               created_at DATETIME(6) NOT NULL,
                               updated_at DATETIME(6) NOT NULL,

                               CONSTRAINT fk_history_cloth_history FOREIGN KEY (history_id) REFERENCES history(id),
                               CONSTRAINT fk_history_cloth_cloth FOREIGN KEY (cloth_id) REFERENCES cloth(id)
);
CREATE INDEX idx_history_cloth_history_id ON history_cloth (history_id);


CREATE TABLE history_image (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               image_url VARCHAR(255) NOT NULL UNIQUE,
                               history_id BIGINT NOT NULL,
                               created_at DATETIME(6) NOT NULL,
                               updated_at DATETIME(6) NOT NULL,

                               CONSTRAINT fk_history_image_history FOREIGN KEY (history_id) REFERENCES history(id)
);
CREATE INDEX idx_history_created_at ON history_image (history_id, created_at);


CREATE TABLE member_like (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             member_id BIGINT NOT NULL,
                             history_id BIGINT NOT NULL,
                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6) NOT NULL,

                             CONSTRAINT fk_member_like_member FOREIGN KEY (member_id) REFERENCES member(id),
                             CONSTRAINT fk_member_like_history FOREIGN KEY (history_id) REFERENCES history(id),
                             CONSTRAINT uk_member_history UNIQUE (member_id, history_id)
);
CREATE INDEX idx_member_like_member_id ON member_like (member_id);
CREATE INDEX idx_member_like_history_id ON member_like (history_id);


CREATE TABLE hashtag_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 hashtag_id BIGINT NOT NULL,
                                 history_id BIGINT NOT NULL,
                                 created_at DATETIME(6) NOT NULL,
                                 updated_at DATETIME(6) NOT NULL,

                                 CONSTRAINT fk_hashtag_history_hashtag FOREIGN KEY (hashtag_id) REFERENCES hashtag(id),
                                 CONSTRAINT fk_hashtag_history_history FOREIGN KEY (history_id) REFERENCES history(id),
                                 CONSTRAINT uk_history_hashtag UNIQUE (history_id, hashtag_id)
);
CREATE INDEX idx_history_id ON hashtag_history (history_id);
CREATE INDEX idx_hashtag_id ON hashtag_history (hashtag_id);


CREATE TABLE block (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       blocker_id BIGINT NOT NULL,
                       blocked_id BIGINT NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,

                       CONSTRAINT fk_block_blocker FOREIGN KEY (blocker_id) REFERENCES member(id),
                       CONSTRAINT fk_block_blocked FOREIGN KEY (blocked_id) REFERENCES member(id),

                       CONSTRAINT uk_blocker_blocked UNIQUE (blocker_id, blocked_id)
);


CREATE TABLE follow (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        following_user_id BIGINT NOT NULL,
                        followed_user_id BIGINT NOT NULL,
                        created_at DATETIME(6) NOT NULL,
                        updated_at DATETIME(6) NOT NULL,

                        CONSTRAINT fk_follow_following_user FOREIGN KEY (following_user_id) REFERENCES member(id),
                        CONSTRAINT fk_follow_followed_user FOREIGN KEY (followed_user_id) REFERENCES member(id),

                        CONSTRAINT uk_following_followed UNIQUE (following_user_id, followed_user_id)
);




CREATE TABLE clokey_notification (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                     member_id BIGINT NOT NULL,
                                     content VARCHAR(50) NOT NULL,
                                     notification_image_url VARCHAR(255) NOT NULL,
                                     redirect_info VARCHAR(255) NOT NULL,

                                     redirect_type VARCHAR(30) NOT NULL CHECK (
                                         redirect_type IN ('HISTORY_REDIRECT', 'MEMBER_REDIRECT')
                                         ),

                                     read_status VARCHAR(15) NOT NULL DEFAULT 'NOT_READ' CHECK (
                                         read_status IN ('READ', 'NOT_READ')
                                         ),

                                     created_at DATETIME(6) NOT NULL,
                                     updated_at DATETIME(6) NOT NULL,

                                     CONSTRAINT fk_clokey_notification_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE recommendation (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                content_id BIGINT,
                                image_url VARCHAR(500),
                                temperature DOUBLE,
                                clothes_ids VARCHAR(1000),
                                hashtag VARCHAR(500),
                                sub_title VARCHAR(500),

                                news_type VARCHAR(20) NOT NULL CHECK (
                                    news_type IN ('NEWS', 'UPDATE', 'RECOMMEND') -- 💡 가라 enum 값, 실제 enum 값으로 수정 가능
                                    ),

                                member_id BIGINT NOT NULL,
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,

                                CONSTRAINT fk_recommendation_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE comment_report (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                comment_report_type VARCHAR(30) NOT NULL, -- 예: 가라 enum 값, 추후 CHECK 제약으로 확장 가능
                                report_status VARCHAR(15) NOT NULL DEFAULT 'UNCHECKED' CHECK (
                                    report_status IN ('APPROVED', 'DISAPPROVED', 'UNCHECKED')
                                    ),

                                comment_id BIGINT NOT NULL,
                                member_id BIGINT NOT NULL,

                                content VARCHAR(200),
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,

                                CONSTRAINT fk_comment_report_comment FOREIGN KEY (comment_id) REFERENCES comment(id),
                                CONSTRAINT fk_comment_report_member FOREIGN KEY (member_id) REFERENCES member(id)
);


CREATE TABLE history_report (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                history_report_type VARCHAR(30) NOT NULL, -- 가라 enum 값 (예: 'SPAM', 'INAPPROPRIATE' 등)
                                report_status VARCHAR(15) NOT NULL DEFAULT 'UNCHECKED' CHECK (
                                    report_status IN ('APPROVED', 'DISAPPROVED', 'UNCHECKED')
                                    ),

                                history_id BIGINT NOT NULL,
                                member_id BIGINT NOT NULL,

                                content VARCHAR(200),
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,

                                CONSTRAINT fk_history_report_history FOREIGN KEY (history_id) REFERENCES history(id),
                                CONSTRAINT fk_history_report_member FOREIGN KEY (member_id) REFERENCES member(id)
);


CREATE TABLE profile_report (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                reporter_id BIGINT NOT NULL,
                                reported_id BIGINT NOT NULL,

                                profile_report_type VARCHAR(30) NOT NULL CHECK (
                                    profile_report_type IN ('FAKE', 'SPAM_OR_PROMOTION', 'INAPPROPRIATE', 'ETC')
                                    ),

                                content VARCHAR(200),

                                report_status VARCHAR(15) NOT NULL DEFAULT 'UNCHECKED' CHECK (
                                    report_status IN ('APPROVED', 'DISAPPROVED', 'UNCHECKED')
                                    ),

                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL,

                                CONSTRAINT fk_profile_report_reporter FOREIGN KEY (reporter_id) REFERENCES member(id),
                                CONSTRAINT fk_profile_report_reported FOREIGN KEY (reported_id) REFERENCES member(id)
);


CREATE TABLE member_term (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,

                             member_id BIGINT NOT NULL,
                             term_id BIGINT NOT NULL,

                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6) NOT NULL,

                             CONSTRAINT fk_member_term_member FOREIGN KEY (member_id) REFERENCES member(id),
                             CONSTRAINT fk_member_term_term FOREIGN KEY (term_id) REFERENCES term(id)
);

