import React, { useState } from 'react'
import {
    Container, Row, Col, Card, CardBody, Form,
    FormGroup, Label, Input, Button, Alert
} from 'reactstrap';
import '../css/Login.css'
import { Lock, LogIn } from 'lucide-react';
import { signIn } from '../Services/User-service';
import { Link, useNavigate } from 'react-router-dom';

const Login = () => {

    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const handleStandardLogin = async (e) => {
        e.preventDefault();
        try {
            // This calls your Gateway's standard auth endpoint
            const data = await signIn(credentials);
            // On success, the Gateway sends the HttpOnly cookies
            console.log("LoggedIn User:- ",data);
            
            window.location.href = '/dashboard';
        } catch (err) {
            alert("Invalid credentials!");
        }
    };

    const handleGoogleLogin = () => {
        // Redirect directly to the Gateway's OAuth2 start point
        window.location.href = 'http://localhost:9096/oauth2/authorization/google';
    };

    return (
        <div className="auth-wrapper bg-light min-vh-100 d-flex align-items-center">
            <Container>
                <Row className="justify-content-center">
                    <Col md={6} lg={5}>
                        <Card className="shadow-lg border-0">
                            <CardBody className="p-5">
                                <div className="text-center mb-4">
                                    <div className="icon-badge mb-3">
                                        <Lock size={32} className="text-primary" />
                                    </div>
                                    <h3 className="fw-bold">Welcome Back</h3>
                                    <p className="text-muted">Enter your credentials to access your links</p>
                                </div>

                                {error && <Alert color="danger" className="py-2 small">{error}</Alert>}

                                <Form onSubmit={handleStandardLogin}>
                                    <FormGroup>
                                        <Label for="username" className="small fw-semibold">Username</Label>
                                        <Input
                                            id="username"
                                            type="text"
                                            placeholder="Enter username"
                                            className="form-control-lg"
                                            onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                                            required
                                        />
                                    </FormGroup>
                                    <FormGroup className="mb-4">
                                        <Label for="password" className="small fw-semibold">Password</Label>
                                        <Input
                                            id="password"
                                            type="password"
                                            placeholder="••••••••"
                                            className="form-control-lg"
                                            onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                                            required
                                        />
                                    </FormGroup>

                                    <Button color="primary" block size="lg" className="fw-bold mb-3 shadow-sm">
                                        <LogIn size={18} className="me-2" /> Sign In
                                    </Button>
                                </Form>

                                <div className="position-relative my-4">
                                    <hr />
                                    <span className="position-absolute top-50 start-50 translate-middle bg-white px-3 text-muted small">
                                        OR CONTINUE WITH
                                    </span>
                                </div>

                                <Button
                                    outline
                                    color="secondary"
                                    block
                                    size="lg"
                                    className="d-flex align-items-center justify-content-center fw-semibold"
                                    onClick={handleGoogleLogin}
                                >
                                    <img
                                        src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                        alt="Google"
                                        style={{ width: '18px', marginRight: '10px' }}
                                    />
                                    Google Account
                                </Button>

                                <div className="text-center mt-4">
                                    <p className="small text-muted mb-0">
                                        Don't have an account? <Link to={"/signup"} tag={'a'} className="text-decoration-none">Sign Up</Link>
                                    </p>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Login;