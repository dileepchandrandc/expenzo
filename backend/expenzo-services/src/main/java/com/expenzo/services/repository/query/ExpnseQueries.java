package com.expenzo.services.repository.query;

public class ExpnseQueries {
    public static final String FETCH_EXPENSES_BY_YEAR_MONTH = """
        select
            t.*,
            ec.id as category_id,
            ec.name as category_name,
            ec.is_system_generated as category_system_generated
        from
            "transaction" t
            left join expense_category ec on ec.id = t.expense_category_id
        where
            t.type = 'EXPENSE'
            and t.user_id = ?
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
}
