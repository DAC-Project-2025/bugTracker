import React, { useState, useEffect } from 'react';
import './HomePage.css';

const Header = () => {
  const [isScrolled, setIsScrolled] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <header className={`header ${isScrolled ? 'header-scrolled' : ''}`}>
      <div className="header-container">
        <div className="logo-container">
          <span className="logo-text">Bug Tracker</span>
        </div>

        <nav className="nav-menu">
          <a href="#home" className="nav-link">Home</a>
          <a href="#about" className="nav-link">About</a>
          <a href="#contact" className="nav-link">Contact</a>
        </nav>

        <div className="auth-buttons">
          <a href="/login" className="btn-login">Login</a>
          <a href="/signup" className="btn-signup">Sign Up</a>
        </div>
      </div>
    </header>
  );
};

const HeroSection = () => (
  <section id="home" className="hero-section">
    <div className="hero-container">
      <div className="hero-text">
        <h1 className="hero-title">
          Track Bugs.<br />
          <span className="text-gradient">Ship Faster.</span>
        </h1>
        <p className="hero-description">
          The advanced bug tracking solution designed for companies to streamline workflows, enhance collaboration, and deliver flawless software.
        </p>
        <div className="hero-buttons">
          <button className="btn-secondary">View Demo</button>
        </div>
      </div>
    </div>
  </section>
);

const AboutSection = () => (
  <section id="about" className="section-container">
    <h2>About BugTrack Pro</h2>
    <p>
      BugTrack Pro is dedicated to helping companies streamline their software development process by providing a powerful, secure, and easy-to-use bug tracking platform.
    </p>
    <p>
      Our mission is to empower teams to ship better software faster, with full transparency and collaboration.
    </p>
  </section>
);

const ContactSection = () => (
  <section id="contact" className="section-container">
    <h2>Contact Us</h2>
    <p>
      Have questions or want to schedule a demo? Reach out to our team and we’ll get back to you promptly.
    </p>
    <form className="contact-form" onSubmit={e => e.preventDefault()}>
      <input type="text" placeholder="Your Name" required className="input-field" />
      <input type="email" placeholder="Your Email" required className="input-field" />
      <textarea placeholder="Your Message" required className="input-field textarea-field" />
      <button type="submit" className="btn-primary">Send Message</button>
    </form>
  </section>
);

const Footer = () => (
  <footer className="footer">
    <div className="footer-container">
      <p>&copy; 2024 Bug Tracker. All rights reserved.</p>
    </div>
  </footer>
);

export default function Home() {
  return (
    <>
      <Header />
      <main>
        <HeroSection />
        <AboutSection />
        <ContactSection />
      </main>
      <Footer />
    </>
  );
}
