-- Dev admin: business SY, employee id 000admin, password qwerty!23 (SHA-256, uppercase hex)

INSERT INTO dashboard_role (role_id, role_name, user_id)
SELECT '1', 'Administrator', '000admin'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_role WHERE role_id = '1');

INSERT INTO dashboard_user (user_id, business_code, login_name, user_name, user_password, user_status, rbac_policy, user_state_info, del_cd)
SELECT '000admin', 'SY', 'admin', 'Local Administrator',
       '1009F79F4C5940C17CA3C5F864F6DEDB21EF2B95D4075A6D93E6EB8B08E75244', 'Active', 0, '0', 'N'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_user WHERE user_id = '000admin' AND business_code = 'SY');

INSERT INTO dashboard_user_role (user_id, business_code, role_id)
SELECT '000admin', 'SY', '1'
WHERE NOT EXISTS (SELECT 1 FROM dashboard_user_role WHERE user_id = '000admin' AND business_code = 'SY' AND role_id = '1');

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '1', 1, 'edit', 'N' WHERE NOT EXISTS (SELECT 1 FROM dashboard_role_res WHERE role_id = '1' AND res_type = '1');

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '2', 1, 'edit', 'N' WHERE NOT EXISTS (SELECT 1 FROM dashboard_role_res WHERE role_id = '1' AND res_type = '2');

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
SELECT '1', '3', 1, 'edit', 'N' WHERE NOT EXISTS (SELECT 1 FROM dashboard_role_res WHERE role_id = '1' AND res_type = '3');
