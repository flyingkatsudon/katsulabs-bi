-- 개발 시드 (admin01 / admin123 = SHA-256 대문자 HEX)
INSERT INTO dashboard_role (role_id, role_name, user_id)
VALUES ('1', 'Admin', 'admin01');

INSERT INTO dashboard_user (
    user_id, business_code, login_name, user_name, user_password,
    user_status, rbac_policy, user_state_info, del_cd
) VALUES (
    'admin01', 'SH', 'admin01', 'Admin',
    '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9',
    'active', 0, '0', 'N'
);

INSERT INTO dashboard_user_role (user_id, business_code, role_id)
VALUES ('admin01', 'SH', '1');

INSERT INTO dashboard_role_res (role_id, res_type, res_id, permission, del_cd)
VALUES ('1', '1', 1, 'write', 'N');

INSERT INTO dashboard_category (category_name, user_id)
VALUES ('Demo', 'admin01');

INSERT INTO dashboard_board (user_id, category_id, board_name, layout_json)
VALUES (
    'admin01',
    1,
    'Demo Board',
    '{"rows":[{"widgets":[]}]}'
);
