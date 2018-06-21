  Feature: As an api user I want to retrieve latest most stars repositories of last week

    Scenario: Get most rated repos
      When I get the service uri search/repositories/1
      Then the service uri returns status code 200
      And the content type is json
      And the body has json path $ of type object
      And the body has json path $.items of type array
      And the body has json path $.items[0].html_url of type string
      And the body has json path $.items[0].watchers_count of type numeric
      And the body has json path $.items[0].name of type string
      And the body has json path $.items[0].description of type string
      And the body has json path $.items[0].language of type string
      
    Scenario: Get most rated repos negative number of repository in request, returns bad request
      When I get the service uri search/repositories/-1
      Then the service uri returns status code 400
      And the content type is json
      And the body has json path $ of type object
      And the body has json path $.message of type string
      And the body has json path $.message that is equal to Requested number of repository cannot be negative integer
      
    Scenario: Get oldest accounts with zero followers
      When I get the service uri search/accounts/1
      Then the service uri returns status code 200
      And the content type is json
      And the body has json path $ of type object
      And the body has json path $.items of type array
      And the body has json path $.items[0].id of type numeric
      And the body has json path $.items[0].login of type string
      And the body has json path $.items[0].html_url of type string
      
    Scenario: Get oldest accounts with zero followers negative number of accounts in request, returns bad request
      When I get the service uri search/accounts/-1
      Then the service uri returns status code 400
      And the content type is json
      And the body has json path $ of type object
      And the body has json path $.message of type string
      And the body has json path $.message that is equal to Requested number of accounts cannot be negative integer