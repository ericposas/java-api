-- select all first row results of a user's saved addresses
select
    *
from
    (
        -- select all of a user's addresses, include row number of each result
        -- partitioned by user id
        select
            u.id,
            u.firstname,
            a.*,
            ROW_NUMBER() OVER (partition by u.id) as rownum
        from
            addresses a
            join usersaddresses ua on a.id = ua.address_id
            join users u on ua.user_id = u.id
    ) e
where
    e.rownum = 1;

-- get all addresses for a particular user id
select
    u.id,
    u.firstname,
    a.*,
    ua.isprimary
from
    addresses a
    join usersaddresses ua on a.id = ua.address_id
    join users u on ua.user_id = u.id
where
    u.id = 5;

-- note:
-- placing the 'isprimary' column in USERSADDRESSES allowed us to track address
-- ids assigned to a user in a HashMap and mark the first as primary
--
--
-- select all users mapped to addresses where the user has over 2 addresses
-- using the ROW_NUMBER() window function to see number of returned rows
-- partitioned by user_id ("partitioned by" just means rows grouped vertically)
with withrowcnt as (
    select
        u.id as user_id,
        u.firstname,
        u.lastname,
        a.id as address_id,
        a.*,
        ua.isprimary,
        row_number() over (partition by u.id) as rowcnt
    from
        addresses a
        join usersaddresses ua on a.id = ua.address_id
        join users u on ua.user_id = u.id
    order by
        u.id
)
select
    *
from
    withrowcnt
where
    rowcnt > 2;

-- using this with query as a subquery, we can then get all address information
-- for every user that has at least 3 mapped addresses
select
    u.*,
    a.*
from
    users u
    join usersaddresses ua on u.id = ua.user_id
    join addresses a on ua.address_id = a.id
where
    u.id in (
        select
            user_id
        from
            (
                with withrowcnt as (
                    select
                        u.id as user_id,
                        u.firstname,
                        u.lastname,
                        a.id as address_id,
                        a.*,
                        ua.isprimary,
                        row_number() over (partition by u.id) as rowcnt
                    from
                        addresses a
                        join usersaddresses ua on a.id = ua.address_id
                        join users u on ua.user_id = u.id
                    order by
                        u.id
                )
                select
                    *
                from
                    withrowcnt
                where
                    rowcnt > 2
            ) sub
    )
order by
    u.id;