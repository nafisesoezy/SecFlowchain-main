-- update context mapping info table with owner id (deployment id) for per process instance strategies
alter table ContextMappingInfo add OWNER_ID varchar(255);
update ContextMappingInfo set OWNER_ID = (select externalId from ProcessInstanceLog where processInstanceId = cast(CONTEXT_ID as bigint));

create table AuditTaskImpl (
        id bigint generated by default as identity,
        activationTime date,
        actualOwner varchar(255),
        createdBy varchar(255),
        createdOn date,
        deploymentId varchar(255),
        description varchar(255),
        dueDate date,
        name varchar(255),
        parentId bigint not null,
        priority integer not null,
        processId varchar(255),
        processInstanceId bigint not null,
        processSessionId integer not null,
        status varchar(255),
        taskId bigint,
        primary key (id));

ALTER TABLE SessionInfo DROP PRIMARY KEY;
ALTER TABLE SessionInfo ALTER COLUMN id bigint IDENTITY;
ALTER TABLE AuditTaskImpl ALTER COLUMN processSessionId bigint;
ALTER TABLE AuditTaskImpl ALTER COLUMN activationTime timestamp;
ALTER TABLE AuditTaskImpl ALTER COLUMN createdOn timestamp;
ALTER TABLE AuditTaskImpl ALTER COLUMN dueDate timestamp;
ALTER TABLE ContextMappingInfo ALTER COLUMN KSESSION_ID bigint;
ALTER TABLE Task ALTER COLUMN processSessionId bigint;

CREATE TABLE DeploymentStore (
    id bigint generated by default as identity,
    attributes varchar(255),
    DEPLOYMENT_ID varchar(255),
    deploymentUnit clob,
    state integer,
    updateDate timestamp,
    PRIMARY KEY (id)
);

ALTER TABLE DeploymentStore
        ADD CONSTRAINT UK_DeploymentStore_1 UNIQUE (DEPLOYMENT_ID);

ALTER TABLE ProcessInstanceLog ADD processInstanceDescription varchar(255);
ALTER TABLE RequestInfo ADD owner varchar(255);
ALTER TABLE Task ADD description varchar(255);
ALTER TABLE Task ADD name varchar(255);
ALTER TABLE Task ADD subject varchar(255);

-- update all tasks with its name, subject and description
UPDATE Task t SET name = (SELECT shortText FROM I18NText WHERE Task_Names_Id = t.id);
UPDATE Task t SET subject = (SELECT shortText FROM I18NText WHERE Task_Subjects_Id = t.id);
UPDATE Task t SET description = (SELECT shortText FROM I18NText WHERE Task_Descriptions_Id = t.id);

INSERT INTO AuditTaskImpl (activationTime, actualOwner, createdBy, createdOn, deploymentId, description, dueDate, name, parentId, priority, processId, processInstanceId, processSessionId, status, taskId)
SELECT activationTime, actualOwner_id, createdBy_id, createdOn, deploymentId, description, expirationTime, name, parentId, priority,processId, processInstanceId, processSessionId, status, id 
FROM Task;

ALTER TABLE TaskEvent ADD COLUMN workItemId bigint;
ALTER TABLE TaskEvent ADD COLUMN processInstanceId bigint;
UPDATE TaskEvent t SET workItemId = (SELECT workItemId FROM Task WHERE id = t.taskId);
UPDATE TaskEvent t SET processInstanceId = (SELECT processInstanceId FROM Task WHERE id = t.taskId);