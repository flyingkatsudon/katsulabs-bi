-- CBoard schema extensions (run after 01-schema.sql)
ALTER TABLE dashboard_user ADD COLUMN business_code VARCHAR(10) DEFAULT '' AFTER user_id;
ALTER TABLE dashboard_user ADD COLUMN rbac_policy VARCHAR(255) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN user_state_info VARCHAR(255) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN del_cd VARCHAR(10) DEFAULT NULL;
ALTER TABLE dashboard_user ADD COLUMN user_last_date DATETIME DEFAULT NULL;
ALTER TABLE dashboard_user_role ADD COLUMN business_code VARCHAR(10) DEFAULT '' AFTER user_id;
ALTER TABLE dashboard_role_res ADD COLUMN del_cd VARCHAR(10) DEFAULT 'N';
