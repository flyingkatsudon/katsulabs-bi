-- 단일 테넌트: business_code 컬럼 및 복합 PK 제거

DELETE FROM dashboard_user_role ur
 WHERE EXISTS (
       SELECT 1 FROM dashboard_user u
        WHERE u.user_id = ur.user_id AND u.business_code = 'SH'
   )
   AND ur.business_code <> 'SH';

DELETE FROM dashboard_user u
 WHERE u.business_code <> 'SH'
   AND EXISTS (
       SELECT 1 FROM dashboard_user u2
        WHERE u2.user_id = u.user_id AND u2.business_code = 'SH'
   );

DELETE FROM dashboard_user WHERE business_code <> 'SH';

ALTER TABLE dashboard_user_role DROP COLUMN IF EXISTS business_code;

ALTER TABLE dashboard_user DROP PRIMARY KEY;
ALTER TABLE dashboard_user DROP COLUMN IF EXISTS business_code;
ALTER TABLE dashboard_user ADD PRIMARY KEY (user_id);
