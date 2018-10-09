drop table if exists oauth_client_details;
create table oauth_client_details (
  client_id               VARCHAR(4096) PRIMARY KEY,
  resource_ids            VARCHAR(4096),
  client_secret           VARCHAR(4096),
  scope                   VARCHAR(4096),
  authorized_grant_types  VARCHAR(4096),
  web_server_redirect_uri VARCHAR(4096),
  authorities             VARCHAR(4096),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(4096)
);