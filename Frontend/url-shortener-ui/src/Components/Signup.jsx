import React, { useState } from 'react'
import {
    Container, Row, Col, Card, CardBody, Form,
    FormGroup, Label, Input, Button, Alert
} from 'reactstrap';
import { UserPlus, Mail, User } from 'lucide-react';
import { signup } from '../Services/User-service';
import '../css/Signup.css';
import { Link } from 'react-router-dom';

const Signup = () => {
    const [formData, setFormData] = useState({
        fullName: '',
        username: '',
        password: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSignup = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        setLoading(true);
        try {
            // Adjust the endpoint to match your USER-SERVICE registration route
            const data = await signup(formData);
            console.log("Data Save Successfully!",data);
            
            window.location.href = '/login?registered=true';
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed. Username might be taken.');
            console.error("User Not Register:- ",err);
        } finally {
            setLoading(false);
        }
    };

    const handleGoogleSignup = () => {
        window.location.href = 'http://localhost:9096/oauth2/authorization/google';
    };

    return (
        <div className="auth-wrapper bg-light min-vh-100 d-flex align-items-center py-5">
            <Container>
                <Row className="justify-content-center">
                    <Col md={8} lg={6}>
                        <Card className="shadow-lg border-0 overflow-hidden">
                            <Row className="g-0">
                                <Col p={5} className="bg-white">
                                    <CardBody className="p-4 p-md-5">
                                        <div className="text-center mb-4">
                                            <div className="icon-badge mb-3 bg-soft-success">
                                                <UserPlus size={32} className="text-success" />
                                            </div>
                                            <h3 className="fw-bold">Create Account</h3>
                                            <p className="text-muted">Join SecurePath to manage your links</p>
                                        </div>

                                        {error && <Alert color="danger" className="py-2 small">{error}</Alert>}

                                        <Form onSubmit={handleSignup}>
                                            <FormGroup className="mb-3">
                                                <Label className="small fw-semibold">Full Name</Label>
                                                <div className="input-group">
                                                    <span className="input-group-text bg-light border-end-0"><User size={18} className="text-muted" /></span>
                                                    <Input
                                                        type="text"
                                                        placeholder="Hemant Barole"
                                                        className="border-start-0 ps-0"
                                                        onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                                                        required
                                                    />
                                                </div>
                                            </FormGroup>

                                            <FormGroup className="mb-3">
                                                <Label className="small fw-semibold">Email / Username</Label>
                                                <div className="input-group">
                                                    <span className="input-group-text bg-light border-end-0"><Mail size={18} className="text-muted" /></span>
                                                    <Input
                                                        type="email"
                                                        placeholder="name@company.com"
                                                        className="border-start-0 ps-0"
                                                        onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                                                        required
                                                    />
                                                </div>
                                            </FormGroup>

                                            <Row>
                                                <Col md={6}>
                                                    <FormGroup className="mb-3">
                                                        <Label className="small fw-semibold">Password</Label>
                                                        <Input
                                                            type="password"
                                                            placeholder="••••••••"
                                                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                                            required
                                                        />
                                                    </FormGroup>
                                                </Col>
                                                <Col md={6}>
                                                    <FormGroup className="mb-4">
                                                        <Label className="small fw-semibold">Confirm</Label>
                                                        <Input
                                                            type="password"
                                                            placeholder="••••••••"
                                                            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                                                            required
                                                        />
                                                    </FormGroup>
                                                </Col>
                                            </Row>

                                            <Button color="primary" block size="lg" className="fw-bold mb-3 shadow-sm" disabled={loading}>
                                                {loading ? 'Creating Account...' : 'Get Started Free'}
                                            </Button>
                                        </Form>

                                        <div className="position-relative my-4">
                                            <hr />
                                            <span className="position-absolute top-50 start-50 translate-middle bg-white px-3 text-muted small">
                                                OR
                                            </span>
                                        </div>

                                        <Button
                                            outline
                                            color="secondary"
                                            block
                                            size="lg"
                                            className="d-flex align-items-center justify-content-center fw-semibold py-2"
                                            onClick={handleGoogleSignup}
                                        >
                                            <img
                                                src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                                                alt="Google"
                                                style={{ width: '18px', marginRight: '10px' }}
                                            />
                                            Sign up with Google
                                        </Button>

                                        <p className="text-center small text-muted mt-4">
                                            By signing up, you agree to our <a href="#" className="text-decoration-none">Terms of Service</a>
                                        </p>
                                    </CardBody>
                                </Col>
                            </Row>
                        </Card>
                        <div className="text-center mt-4">
                            <p className="text-muted">
                                Already have an account? <Link to={"/login"} className="text-primary fw-bold text-decoration-none">Log in</Link>
                            </p>
                        </div>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default Signup;