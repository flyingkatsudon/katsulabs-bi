-- Admin user for local Docker dev (password: admin123 = SHA-256 uppercase)
INSERT INTO dashboard_role (role_id, role_name, user_id) VALUES ('1', 'Admin', 'admin01')
  ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

INSERT INTO dashboard_user (user_id, business_code, login_name, user_name, user_password, user_status, rbac_policy, user_state_info, del_cd)
 VALUES ('admin01', 'SH', 'admin01', 'Admin', '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9', 'active', '0', '0', 'N')
  ON DUPLICATE KEY UPDATE login_name = VALUES(login_name), user_password = VALUES(user_password);

INSERT INTO dashboard_user_role (user_id, business_code, role_id) VALUES ('admin01', 'SH', '1')
  ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd) VALUES ('1', '1', 1, 'write', 'N')
  ON DUPLICATE KEY UPDATE permission = VALUES(permission);
