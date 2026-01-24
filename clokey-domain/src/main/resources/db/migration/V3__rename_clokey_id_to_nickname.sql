-- clokey_id 컬럼을 nickname으로 변경하고 UK 추가
ALTER TABLE member RENAME COLUMN clokey_id TO nickname;
ALTER TABLE member ALTER COLUMN nickname SET DATA TYPE VARCHAR(30);
ALTER TABLE member ADD CONSTRAINT uk_member_nickname UNIQUE (nickname);

