#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Role {
    Patient,
    ParentGuardian,
    Therapist,
    Supervisor,
    Doctor,
    Admin,
    System,
}

impl Role {
    pub fn from_header(value: &str) -> Option<Self> {
        match value.to_ascii_uppercase().as_str() {
            "PATIENT" => Some(Self::Patient),
            "PARENT_GUARDIAN" => Some(Self::ParentGuardian),
            "THERAPIST" => Some(Self::Therapist),
            "SUPERVISOR" => Some(Self::Supervisor),
            "DOCTOR" => Some(Self::Doctor),
            "ADMIN" => Some(Self::Admin),
            "SYSTEM" => Some(Self::System),
            _ => None,
        }
    }

    pub fn can_review(self) -> bool {
        matches!(self, Self::Supervisor | Self::Admin | Self::System)
    }

    pub fn can_access_doctor_portal(self) -> bool {
        matches!(self, Self::Doctor | Self::Supervisor | Self::Admin | Self::System)
    }
}
