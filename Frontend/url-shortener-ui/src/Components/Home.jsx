import { 
    Container, Row, Col, Button, Nav, Navbar, 
    NavbarBrand, NavItem, NavLink, Card, CardBody 
} from 'reactstrap';
import { Link } from 'react-router-dom';
import { Shield, Zap, BarChart2, Link as LinkIcon, ChevronRight } from 'lucide-react';
import React from 'react'
import "../css/Home.css";

const Home = () => {
  return (
        <div className="landing-page">
            {/* Elegant Navbar */}
            <Navbar dark expand="md" className="py-3 px-lg-5 sticky-top bg-dark-glass">
                <NavbarBrand tag={Link} to="/" className="fw-bold d-flex align-items-center">
                    <div className="brand-icon me-2">S</div>
                    SecurePath
                </NavbarBrand>
                <Nav className="ms-auto" navbar>
                    <NavItem>
                        <NavLink tag={Link} to="/login" className="text-white opacity-75 hover-opacity-100 me-3">Sign In</NavLink>
                    </NavItem>
                    <NavItem>
                        <Button tag={Link} to="/login" color="primary" className="rounded-pill px-4 fw-semibold shadow-sm">
                            Get Started
                        </Button>
                    </NavItem>
                </Nav>
            </Navbar>

            {/* Hero Section */}
            <header className="hero-section text-center text-white d-flex align-items-center">
                <Container>
                    <Row className="justify-content-center">
                        <Col lg={9}>
                            <div className="badge-announcement mb-4">
                                <span className="badge rounded-pill bg-primary-soft text-primary px-3 py-2">
                                    New: OAuth2.0 Integration Live 🚀
                                </span>
                            </div>
                            <h1 className="display-3 fw-extrabold mb-3">
                                Shorten URLs. <span className="text-gradient">Secure Data.</span>
                            </h1>
                            <p className="lead text-muted-custom mb-5">
                                The microservices-driven URL shortener built for developers who care about security, 
                                real-time analytics, and sub-millisecond redirection.
                            </p>
                            <div className="d-flex flex-column flex-sm-row justify-content-center gap-3">
                                <Button tag={Link} to="/login" color="primary" size="lg" className="px-5 py-3 rounded-pill fw-bold">
                                    Create Free Link <ChevronRight size={20} className="ms-1" />
                                </Button>
                                <Button outline color="light" size="lg" className="px-5 py-3 rounded-pill fw-bold border-opacity-25">
                                    View Documentation
                                </Button>
                            </div>
                        </Col>
                    </Row>
                </Container>
            </header>

            {/* Features Grid */}
            <section className="features-section py-5">
                <Container className="py-5">
                    <Row className="g-4">
                        {[
                            { icon: <Shield className="text-primary" />, title: "Enterprise Security", text: "HttpOnly cookies and internal secret handshakes ensure your data is invisible to attackers." },
                            { icon: <Zap className="text-warning" />, title: "Lightning Fast", text: "Powered by Redis caching and Reactive Spring WebFlux for instant redirects." },
                            { icon: <BarChart2 className="text-success" />, title: "Deep Analytics", text: "Track clicks, browsers, and geographic data with our dedicated analytics microservice." },
                            { icon: <LinkIcon className="text-info" />, title: "Custom Aliases", text: "Create memorable branded links that increase click-through rates by up to 34%." }
                        ].map((feature, idx) => (
                            <Col md={6} lg={3} key={idx}>
                                <Card className="h-100 border-0 shadow-sm feature-card">
                                    <CardBody className="p-4">
                                        <div className="feature-icon-wrapper mb-3">{feature.icon}</div>
                                        <h5 className="fw-bold">{feature.title}</h5>
                                        <p className="text-muted small mb-0">{feature.text}</p>
                                    </CardBody>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </Container>
            </section>

            <footer className="py-5 text-center bg-dark text-white-50 border-top border-secondary border-opacity-10">
                <Container>
                    <p className="small mb-0">© 2026 SecurePath. Built with Spring Boot, React, and Redis.</p>
                </Container>
            </footer>
        </div>
    );
};

export default Home;