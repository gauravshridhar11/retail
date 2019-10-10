

# Coding exercise:

On a retail website, the following discounts apply: 
1. If the user is an employee of the store, he gets a 30% discount 
2. If the user is an affiliate of the store, he gets a 10% discount 
3. If the user has been a customer for over 2 years, he gets a 5% discount. 
4. For every $100 on the bill, there would be a $ 5 discount (e.g. for $ 990, you get $ 45 as a discount). 
5. The percentage based discounts do not apply on groceries. 
6. A user can get only one of the percentage based discounts on a bill. 

Write a program with test cases such that given a bill, it finds the net payable amount. Please note the stress is on object oriented approach and test coverage. What we care about: 


* object oriented programming approach 
* unit test (may be using xUnit) and code coverage 
* code to be generic and simple 
* leverage today's best coding practices 
* clear README.md that explains how the code and the test can be run and how the coverage reports can be generated

Getting Started 
1. To start with, you can create one GitHub Repository for the project share it with us. 
2. You need to regularly commit your progress to the GitHub repository and let us know once the exercise is completed. 

# Getting Started
Requirements:

- Apache Maven 3.3
- Java 1.7

Clone and build the project

```bash
    git clone <repo-path>
    cd bill-discounts
    mvn install
```    

# Assumptions
- When applying multiple discounts, first discount is applied to the net, second discount is applied to the discounted net, etc…
- Mutually exclusive discounts are applied first, for example the percentage based ones
- An entire bill can only be for one category, for example one bill can be groceries, another bill might be for clothing, etc...

# Object Oriented Approach
A high level class diagram of my approach is shown below. I've followed an OO approach to implementing the requirements:
- I felt that User and Bill do not need multiple classes as there is not much business logic related to a specific type of user or bill. Instead I used enums to differentiate between the types of users and bills.
- I felt there is a great deal of business logic related to discounts so implemented them in a generic way such that different discounts can be created. I've used a common discounts super class [GenericDiscount.java]( 
- I've modelled and created subclasses to represent each type of discount (e.g. user type, customer period or multiples of the net) and ensured this is done in an OO way.
- I've used interfaces where possible to reduce coupling.


# Test Coverage

I’ve used the [Cobertura](https://github.com/cobertura/cobertura) code coverage maven plugin to ensure the unit tests provide full coverage. I’ve configured the plugin with the ignoreTrivial flag to ignore trivial one line methods such as getters and setters. The current coverage is 100% 

The coverage report will be available inside the target/site/cobertura/index.html directory


