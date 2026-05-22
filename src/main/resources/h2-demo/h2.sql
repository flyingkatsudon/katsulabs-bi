CREATE TABLE dashboard_board (
  board_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  category_id BIGINT DEFAULT NULL,
  board_name varchar(100) NOT NULL,
  layout_json text,
  PRIMARY KEY (board_id)
);

CREATE TABLE dashboard_category (
  category_id BIGINT NOT NULL AUTO_INCREMENT,
  category_name varchar(100) NOT NULL,
  user_id varchar(100) NOT NULL,
  PRIMARY KEY (category_id)
);

CREATE TABLE dashboard_datasource (
  datasource_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  source_name varchar(100) NOT NULL,
  source_type varchar(100) NOT NULL,
  config text,
  PRIMARY KEY (datasource_id)
);

CREATE TABLE dashboard_widget (
  widget_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(100) NOT NULL,
  category_name varchar(100) DEFAULT NULL,
  widget_name varchar(100) DEFAULT NULL,
  data_json text,
  PRIMARY KEY (widget_id)
);

CREATE TABLE dashboard_dataset (
  dataset_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(100) NOT NULL,
  category_name varchar(100) DEFAULT NULL,
  dataset_name varchar(100) DEFAULT NULL,
  data_json text,
  PRIMARY KEY (dataset_id)
);

CREATE TABLE dashboard_user (
  user_id varchar(50) NOT NULL,
  business_code varchar(10) DEFAULT '',
  login_name varchar(100) DEFAULT NULL,
  user_name varchar(100) DEFAULT NULL,
  user_password varchar(100) DEFAULT NULL,
  user_status varchar(100) DEFAULT NULL,
  rbac_policy varchar(255) DEFAULT NULL,
  user_state_info varchar(255) DEFAULT NULL,
  del_cd varchar(10) DEFAULT NULL,
  user_last_date timestamp DEFAULT NULL,
  PRIMARY KEY (user_id)
);

CREATE TABLE dashboard_user_role (
  user_role_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(100) DEFAULT NULL,
  business_code varchar(10) DEFAULT '',
  role_id varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_role_id)
);

CREATE TABLE dashboard_role (
  role_id varchar(100) NOT NULL,
  role_name varchar(100) DEFAULT NULL,
  user_id varchar(50) DEFAULT NULL,
  PRIMARY KEY (role_id)
);

CREATE TABLE dashboard_role_res (
  role_res_id BIGINT NOT NULL AUTO_INCREMENT,
  role_id varchar(100) DEFAULT NULL,
  res_type varchar(100) DEFAULT NULL,
  res_id BIGINT DEFAULT NULL,
  permission varchar(20) DEFAULT NULL,
  del_cd varchar(10) DEFAULT 'N',
  PRIMARY KEY (role_res_id)
);

CREATE TABLE dashboard_job (
  job_id BIGINT NOT NULL AUTO_INCREMENT,
  job_name varchar(200) DEFAULT NULL,
  cron_exp varchar(200) DEFAULT NULL,
  start_date timestamp NULL DEFAULT NULL,
  end_date timestamp NULL DEFAULT NULL,
  job_type varchar(200) DEFAULT NULL,
  job_config text,
  user_id varchar(100) DEFAULT NULL,
  last_exec_time timestamp NULL DEFAULT NULL,
  job_status BIGINT,
  exec_log text,
  PRIMARY KEY (job_id)
);

CREATE TABLE dashboard_board_param (
  board_param_id BIGINT NOT NULL AUTO_INCREMENT,
  user_id varchar(50) NOT NULL,
  board_id BIGINT NOT NULL,
  config text,
  PRIMARY KEY (board_param_id)
);

-- 升级0.4需要执行的
ALTER  TABLE  dashboard_dataset ADD create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER  TABLE  dashboard_dataset ADD update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER  TABLE  dashboard_datasource ADD create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER  TABLE  dashboard_datasource ADD update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER  TABLE  dashboard_widget ADD create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER  TABLE  dashboard_widget ADD update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER  TABLE  dashboard_board ADD create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER  TABLE  dashboard_board ADD update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

MERGE INTO dashboard_role (role_id, role_name, user_id) KEY(role_id) VALUES ('1', 'Admin', 'admin01');
MERGE INTO dashboard_user (user_id, business_code, login_name, user_name, user_password, user_status, rbac_policy, user_state_info, del_cd)
 KEY(user_id) VALUES ('admin01', 'SH', 'admin01', 'Admin', '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9', 'active', '0', '0', 'N');
INSERT INTO dashboard_user_role (user_id, business_code, role_id)
SELECT 'admin01', 'SH', '1' WHERE NOT EXISTS (
  SELECT 1 FROM dashboard_user_role WHERE user_id = 'admin01' AND business_code = 'SH' AND role_id = '1'
);
INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '1', 1, 'write', 'N' WHERE NOT EXISTS (
  SELECT 1 FROM dashboard_role_res WHERE role_id = '1' AND res_type = '1' AND res_id = 1
);

-- Remove duplicate role rows left by older startups (breaks login selectOne)
DELETE FROM dashboard_user_role
WHERE user_role_id > (
  SELECT MIN(ur2.user_role_id) FROM dashboard_user_role ur2
  WHERE ur2.user_id = dashboard_user_role.user_id
    AND ur2.business_code = dashboard_user_role.business_code
    AND ur2.role_id = dashboard_user_role.role_id
);
UPDATE dashboard_user SET rbac_policy = 0, user_state_info = '0'
WHERE user_id = 'admin01' AND business_code = 'SH';