INSERT INTO m_provision_category(category_name, description) VALUES('PASS AND WATCH', 'Pass and watch');
ALTER TABLE m_provision_category ADD COLUMN ordinal int(100) default 1;
UPDATE m_provision_category SET ordinal=2 WHERE id=5;
UPDATE m_provision_category SET ordinal=3 WHERE id=2;
UPDATE m_provision_category SET ordinal=4 WHERE id=3;
UPDATE m_provision_category SET ordinal=5 WHERE id=4;
