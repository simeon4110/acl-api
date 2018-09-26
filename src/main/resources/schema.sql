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

drop table if exists oauth_client_token;
create table oauth_client_token (
  token_id          VARCHAR(4096),
  token             VARBINARY(4096),
  authentication_id VARCHAR(4096) PRIMARY KEY,
  user_name         VARCHAR(4096),
  client_id         VARCHAR(4096)
);

drop table if exists oauth_access_token;
create table oauth_access_token (
  token_id          VARCHAR(4096),
  token             VARBINARY(4096),
  authentication_id VARCHAR(4096) PRIMARY KEY,
  user_name         VARCHAR(4096),
  client_id         VARCHAR(4096),
  authentication    VARBINARY(4096),
  refresh_token     VARCHAR(4096)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token (
  token_id       VARCHAR(4096),
  token          VARBINARY(4096),
  authentication VARBINARY(4096)
);

drop table if exists oauth_code;
create table oauth_code (
  code           VARCHAR(4096),
  authentication VARBINARY(4096)
);

drop table if exists oauth_approvals;
create table oauth_approvals (
  userId    VARCHAR(4096),
  clientId  VARCHAR(4096),
  scope     VARCHAR(4096),
  status    VARCHAR(4096),
  expiresAt TIMESTAMP,
);

drop table if exists client_details;
create table client_details (
  appId                  VARCHAR(4096) PRIMARY KEY,
  resourceIds            VARCHAR(4096),
  appSecret              VARCHAR(4096),
  scope                  VARCHAR(4096),
  grantTypes             VARCHAR(4096),
  redirectUrl            VARCHAR(4096),
  authorities            VARCHAR(4096),
  access_token_validity  INTEGER,
  refresh_token_validity INTEGER,
  additionalInformation  VARCHAR(4096),
  autoApproveScopes      VARCHAR(4096)
);