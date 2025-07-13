describe('Direct API Test', () => {
  it('should test API directly', () => {
    // Test the API directly using cy.request
    cy.request({
      method: 'POST',
      url: 'http://localhost:8088/api/auth/login',
      body: {
        usernameOrEmail: 'jane.smith@techstart.sg',
        password: 'password123',
        rememberMe: false
      },
      headers: {
        'Content-Type': 'application/json'
      }
    }).then((response) => {
      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('accessToken');
      expect(response.body).to.have.property('user');
      cy.log('Direct API call successful');
      cy.log('User: ' + JSON.stringify(response.body.user));
    });
  });

  it('should test with wrong credentials', () => {
    // Test with wrong credentials
    cy.request({
      method: 'POST',
      url: 'http://localhost:8088/api/auth/login',
      body: {
        usernameOrEmail: 'wrong@email.com',
        password: 'wrongpassword',
        rememberMe: false
      },
      headers: {
        'Content-Type': 'application/json'
      },
      failOnStatusCode: false
    }).then((response) => {
      expect(response.status).to.equal(401);
      cy.log('Wrong credentials correctly rejected');
    });
  });
});
