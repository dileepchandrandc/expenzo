package com.expenzo.services.repository.query;

public class SqlQueries {
    public static class Budget {
        public static final String FETCH_BUDGET_REPORT = """
            select
                coalesce(ec.name, 'Uncategorized') AS category_name, 
                ec.id as category_id, 
                sum(t.amount) as total_spent, 
                round((cast(sum(t.amount) / mbec.spend_limit * 100 as numeric)), 2) as budget_usage_percentage,
                mbec.spend_limit as category_spend_limit,
                mb.id as budget_id,
                mb."name" as budget_name,
                mb.spend_limit as budget_spend_limit,
                case
                    when mb.id is not null then true
                    else false
                end as is_category_part_of_budget
            from
                "transaction" t 
                left join expense_category ec on ec.id  = t.expense_category_id 
                left join monthly_budget_expense_category mbec on mbec.category_id  = ec.id 
                left join monthly_budget mb on mb.id  = mbec.budget_id
            where
                t.user_id  = ?
                and t.timestamp >= ?
                and t.timestamp <= ?
            group by t.expense_category_id, ec."name", mbec.spend_limit, mb.id, mb."name", ec.id
            order by (sum(t.amount) / mbec.spend_limit) desc nulls last;
        """;
    }
}
