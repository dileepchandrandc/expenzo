package com.expenzo.services.repository.query;

public class ExpnseQueries {
    public static final String FETCH_EXPENSES_BY_YEAR_MONTH = """
        select
            t.id,
            t.title,
            t.amount,
            t.description,
            t."timestamp",
            ec.id as category_id,
            ec.name as category_name,
            ec.is_system_generated as category_system_generated,
            t.source_type,
            t.source_id,
            coalesce(ps_dc_ba_b."name" , ps_cc_ba_b."name") as source_channel_bank_name,
            coalesce(ps_dc_ba.nick_name, ps_cc_ba.nick_name) as source_channel_bank_account_nick_name
        from
            "transaction" t
            left join expense_category ec on ec.id = t.expense_category_id
            left join credit_card ps_cc on t.source_type = 'CREDIT_CARD' and t.source_id = ps_cc.id
            left join bank_account ps_cc_ba on t.source_type = 'CREDIT_CARD' and ps_cc.bank_account_id = ps_cc_ba.id
            left join bank ps_cc_ba_b on t.source_type = 'CREDIT_CARD' and ps_cc_ba.bank_id = ps_cc_ba_b.id
            left join debit_card ps_dc on t.source_type = 'DEBIT_CARD' and t.source_id = ps_dc.id
            left join bank_account ps_dc_ba on t.source_type = 'DEBIT_CARD' and ps_dc.bank_account_id = ps_dc_ba.id
            left join bank ps_dc_ba_b on t.source_type = 'DEBIT_CARD' and ps_dc_ba.bank_id = ps_dc_ba_b.id
            left join bank_account ps_ba on t.source_type = 'BANK_ACCOUNT' and ps_ba.id = t.source_id
            left join bank ps_ba_b on t.source_type = 'BANK_ACCOUNT' and ps_ba.bank_id = ps_ba_b.id
        where
            t.type = 'EXPENSE'
            and t.user_id = ?
            and t.timestamp >= ?
            and t.timestamp <= ?
        order by t.timestamp desc
        limit ? offset ?
    """;

    public static final String FETCH_EXPENSES_BY_YEAR_MONTH_CATEGORY = """
        select
            t.id,
            t.title,
            t.amount,
            t.description,
            t."timestamp",
            ec.id as category_id,
            ec.name as category_name,
            ec.is_system_generated as category_system_generated,
            t.source_type,
            t.source_id,
            coalesce(ps_dc_ba_b."name" , ps_cc_ba_b."name") as source_channel_bank_name,
            coalesce(ps_dc_ba.nick_name, ps_cc_ba.nick_name) as source_channel_bank_account_nick_name
        from
            "transaction" t
            left join expense_category ec on ec.id = t.expense_category_id
            left join credit_card ps_cc on t.source_type = 'CREDIT_CARD' and t.source_id = ps_cc.id
            left join bank_account ps_cc_ba on t.source_type = 'CREDIT_CARD' and ps_cc.bank_account_id = ps_cc_ba.id
            left join bank ps_cc_ba_b on t.source_type = 'CREDIT_CARD' and ps_cc_ba.bank_id = ps_cc_ba_b.id
            left join debit_card ps_dc on t.source_type = 'DEBIT_CARD' and t.source_id = ps_dc.id
            left join bank_account ps_dc_ba on t.source_type = 'DEBIT_CARD' and ps_dc.bank_account_id = ps_dc_ba.id
            left join bank ps_dc_ba_b on t.source_type = 'DEBIT_CARD' and ps_dc_ba.bank_id = ps_dc_ba_b.id
            left join bank_account ps_ba on t.source_type = 'BANK_ACCOUNT' and ps_ba.id = t.source_id
            left join bank ps_ba_b on t.source_type = 'BANK_ACCOUNT' and ps_ba.bank_id = ps_ba_b.id
        where
            t.type = 'EXPENSE'
            and t.user_id = ?
            and ec.id = ?
            and t.timestamp >= ?
            and t.timestamp <= ?
        order by t.timestamp desc
        limit ? offset ?
    """;

    public static final String FETCH_EXPENSE_GROUPED_BY_CATEGORY = """
        select
            ec.id,
            coalesce(ec.name, 'Uncategorized') as name,
            ec.is_system_generated,
            sum(t.amount) as total_expense
        from
            "transaction" t 
            left join expense_category ec on ec.id = t.expense_category_id
        where 
            t.type = 'EXPENSE' 
            and t.user_id = ?
            and t.timestamp >= ?
            and t.timestamp <= ?
        group by ec.id
        order by sum(t.amount) desc;
    """;
    public static final String FETCH_EXPENSE_BUCKETS = """
        select
            extract(year from t.timestamp) as year,
            extract(month from t.timestamp) as month
        from
            transaction t 
        where
            user_id = ?
        group by extract(year from t.timestamp), extract(month from t.timestamp);
    """;
    public static final String FETCH_MONTHLY_EXPENSE_OVERVIEW = """
        select
            coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense,
            coalesce(round(sum(t.amount) filter (where t.type = 'EXPENSE') / ?, 2), 0) as avg_spent_per_day,
            coalesce(sum(t.amount)  filter (where t.type = 'INCOME'), 0) as total_income
        from
            "transaction" t 
        where 
            (t.type = 'EXPENSE' or t.type = 'INCOME')
            and t.user_id = ?
            and t.timestamp >= ?
            and t.timestamp <= ?;
    """;
    public static final String FETCH_DAILY_SPENDING_TREND = """
        select
            extract(day from t."timestamp") as day,
            sum(t.amount) as total_spent,
            json_agg(json_build_object('id', t.id, 'title', t.title)) as transactions
        from
            "transaction" t 
        where
            t.user_id  = ?
            and t.timestamp >= ?
            and t.timestamp <= ?
        group by extract(day from t."timestamp")
        order by extract(day from t."timestamp");
    """;
}
