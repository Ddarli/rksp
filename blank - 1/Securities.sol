pragma solidity ^0.8.0;

contract Securities {
    struct Security {
        string name;
        uint256 price;
        uint256 quantity;
    }

    mapping(uint256 => Security) public securities;
    mapping(address => mapping(uint256 => uint256)) public balances;
    uint256 public securityCount;

    event SecurityCreated(uint256 id, string name, uint256 price);
    event SecurityPurchased(address buyer, uint256 id, uint256 quantity);
    event SecurityExchanged(address from, address to, uint256 id, uint256 quantity);

    function createSecurity(string memory _name, uint256 _price, uint256 _quantity) public {
        securityCount++;
        securities[securityCount] = Security(_name, _price, _quantity);
        emit SecurityCreated(securityCount, _name, _price);
    }

    function purchaseSecurity(uint256 _id, uint256 _quantity) public payable {
        require(_id <= securityCount && _id > 0, "Invalid security ID");
        Security storage security = securities[_id];
        require(security.quantity >= _quantity, "Not enough securities available");
        require(msg.value >= security.price * _quantity, "Insufficient funds");

        security.quantity -= _quantity;
        balances[msg.sender][_id] += _quantity;

        emit SecurityPurchased(msg.sender, _id, _quantity);
    }

    function exchangeSecurity(address _to, uint256 _id, uint256 _quantity) public {
        require(balances[msg.sender][_id] >= _quantity, "Not enough securities to exchange");

        balances[msg.sender][_id] -= _quantity;
        balances[_to][_id] += _quantity;

        emit SecurityExchanged(msg.sender, _to, _id, _quantity);
    }

    // Новая функция для получения всех ценных бумаг
    function getAllSecurities() public view returns (uint256[] memory, string[] memory, uint256[] memory, uint256[] memory) {
        uint256[] memory ids = new uint256[](securityCount);
        string[] memory names = new string[](securityCount);
        uint256[] memory prices = new uint256[](securityCount);
        uint256[] memory quantities = new uint256[](securityCount);

        for (uint256 i = 1; i <= securityCount; i++) {
            Security storage security = securities[i];
            ids[i-1] = i;
            names[i-1] = security.name;
            prices[i-1] = security.price;
            quantities[i-1] = security.quantity;
        }

        return (ids, names, prices, quantities);
    }
}