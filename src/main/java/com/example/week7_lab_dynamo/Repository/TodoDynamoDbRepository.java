package com.example.week7_lab_dynamo.Repository;

import com.example.week7_lab_dynamo.model.TodoStatus;
import com.example.week7_lab_dynamo.model.TodoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TodoDynamoDbRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final TableSchema<TodoItem> todoTableSchema;
    private final DynamoDbTable<TodoItem> todoTable;

    @Autowired
    public TodoDynamoDbRepository(DynamoDbEnhancedClient enhancedClient, TableSchema<TodoItem> todoTableSchema) {
        this.enhancedClient = enhancedClient;
        this.todoTableSchema = todoTableSchema;
        this.todoTable = enhancedClient.table("TodoItems", todoTableSchema);
    }

    public List<TodoItem> findAll() {
        return todoTable.scan().items().stream().collect(Collectors.toList());
    }

    public List<TodoItem> findByStatus(TodoStatus status) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":statusValue", AttributeValue.builder().s(status.name()).build());

        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#statusAttr", "status");

        Expression filterExpression = Expression.builder()
                .expression("#statusAttr = :statusValue")
                .expressionNames(expressionNames)
                .expressionValues(expressionValues)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression)
                .build();

        PageIterable<TodoItem> pagedResult = todoTable.scan(request);
        return pagedResult.items().stream().collect(Collectors.toList());
    }

    public Optional<TodoItem> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        TodoItem item = todoTable.getItem(key);
        return Optional.ofNullable(item);
    }

    public TodoItem save(TodoItem todoItem) {
        if (todoItem.getId() == null) {
            todoItem.setId(UUID.randomUUID().toString());
        }
        todoTable.putItem(todoItem);
        return todoItem;
    }

    public void deleteById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        todoTable.deleteItem(key);
    }
}