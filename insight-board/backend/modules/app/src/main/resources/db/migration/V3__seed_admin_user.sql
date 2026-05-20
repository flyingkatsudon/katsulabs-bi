-- password: admin (BCrypt)
INSERT INTO dashboard_user (user_id, login_name, user_name, user_password, user_status, business_code)
VALUES (
    '1',
    'admin',
    'Administrator',
    '$2b$10$Ez1Whzxr.F9QP033rqC6zeeVG/PNXSRrTMey90JWVsq3rERcBgnIG',
    'ACTIVE',
    'DEFAULT'
);

INSERT INTO dashboard_role (role_id, role_name, user_id) VALUES ('ADMIN', 'Administrator', '1');
INSERT INTO dashboard_user_role (user_id, role_id) VALUES ('1', 'ADMIN');
