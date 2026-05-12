from dataclasses import dataclass
import csv
import requests
from datetime import datetime, timezone


@dataclass
class Transaction:
    type: str
    title: str
    description: str
    datetime: str
    amount: float
    source_type: str
    source_id: int
    dest_type: str
    dest_id: int
    meta_data: dict

transactions: list = []

def _read_transactions():
    with open("expenses.csv", newline="", encoding="utf-8") as file:
            reader = csv.DictReader(file)
            for row in reader:
                #Create transaction objects
                transactions.append(Transaction(type=row['type'], title=row['title'], description=row['description'], datetime=_format_date(row['datetime']), 
                            amount=row['amount'],source_type=row['source_type'],source_id=row['source_id'],dest_type=row['dest_type'],
                            dest_id=row['dest_id'], meta_data={'expenseCategoryId': row['category_id']}))
def _add_transactions(transaction: Transaction):
    data = {
        "type": transaction.type,
        "amount": transaction.amount,
        "title": transaction.title,
        "description": transaction.description,
        "timestamp": transaction.datetime,
        "metaData": transaction.meta_data
    }
    if transaction.dest_type != '':
        data['destType'] = transaction.dest_type
        data['destId'] = transaction.dest_id
    if transaction.source_type != '':
        data['sourceType'] = transaction.source_type
        data['sourceId'] = transaction.source_id
    headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        'user-id': '1'
    }
    print(data)
    res = requests.post('http://localhost:8080/expenzo-services/transaction', json=data, headers=headers)
    print(res)
    print("Create transaction " + transaction.title)

def main():
    #Load csv file
    _read_transactions()    
    print("Found total " + str(len(transactions)) + " valid transactions")
    for transaction in transactions:
        _add_transactions(transaction=transaction)



def _format_date(datetime_str: str):
    dt = datetime.strptime(datetime_str, "%d-%m-%y")
    dt = dt.replace(
        hour=0,
        minute=0,
        second=0,
        microsecond=00,
        tzinfo=timezone.utc
    )

    # Convert to ISO 8601 format with 'Z'
    return dt.isoformat(timespec="milliseconds").replace("+00:00", "Z")

if __name__ == '__main__':
    main()